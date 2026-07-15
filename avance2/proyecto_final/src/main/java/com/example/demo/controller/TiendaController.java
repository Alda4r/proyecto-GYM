package com.example.demo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.DetallePedido;
import com.example.demo.model.Pedido;
import com.example.demo.model.Producto;
import com.example.demo.model.Usuario;
import com.example.demo.service.PedidoService;
import com.example.demo.service.ProductoService;

import jakarta.servlet.http.HttpSession;

@Controller
public class TiendaController {

    private static final Path UPLOADS_DIR = Paths.get("uploads");

    @Autowired
    private ProductoService productoService;

    @Autowired
    private PedidoService pedidoService;

    /*
     * Helper para obtener el carrito de la sesión.
     * Cada item es un Map con: productoId, nombre, precio, imagenUrl, cantidad, subtotal
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getCart(HttpSession session) {
        List<Map<String, Object>> cart = (List<Map<String, Object>>) session.getAttribute("carrito");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("carrito", cart);
        }
        return cart;
    }

    @GetMapping("/tienda")
    public String verTienda(Model model, HttpSession session) {
        model.addAttribute("productos", productoService.findAllActivos());
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        model.addAttribute("isAdmin", user != null && "admin@gym.com".equalsIgnoreCase(user.getEmail()));
        return "tienda";
    }

    @PostMapping("/tienda/agregar")
    public String agregarAlCarrito(@RequestParam("productoId") Long productoId,
                                   @RequestParam(value = "cantidad", defaultValue = "1") int cantidad,
                                   HttpSession session) {
        Producto prod = productoService.findById(productoId).orElse(null);
        if (prod == null || !prod.isActivo() || prod.getStock() <= 0) {
            return "redirect:/tienda";
        }

        List<Map<String, Object>> cart = getCart(session);
        boolean encontrado = false;

        for (Map<String, Object> item : cart) {
            if (item.get("productoId").equals(productoId)) {
                int nuevaCant = (int) item.get("cantidad") + cantidad;
                if (nuevaCant > prod.getStock()) {
                    nuevaCant = prod.getStock();
                }
                item.put("cantidad", nuevaCant);
                item.put("subtotal", nuevaCant * (double) item.get("precio"));
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            Map<String, Object> item = new HashMap<>();
            item.put("productoId", prod.getId());
            item.put("nombre", prod.getNombre());
            item.put("precio", prod.getPrecio());
            item.put("imagenUrl", prod.getImagenUrl());
            item.put("cantidad", Math.min(cantidad, prod.getStock()));
            item.put("subtotal", prod.getPrecio() * Math.min(cantidad, prod.getStock()));
            cart.add(item);
        }

        return "redirect:/tienda/carrito";
    }

    @GetMapping("/tienda/carrito")
    public String verCarrito(Model model, HttpSession session) {
        List<Map<String, Object>> cart = getCart(session);
        double total = cart.stream().mapToDouble(i -> (double) i.get("subtotal")).sum();
        model.addAttribute("carrito", cart);
        model.addAttribute("total", Math.round(total * 100.0) / 100.0);
        return "carrito";
    }

    @PostMapping("/tienda/eliminar")
    public String eliminarDelCarrito(@RequestParam("productoId") Long productoId,
                                     HttpSession session) {
        List<Map<String, Object>> cart = getCart(session);
        cart.removeIf(i -> i.get("productoId").equals(productoId));
        return "redirect:/tienda/carrito";
    }

    @PostMapping("/tienda/actualizar")
    public String actualizarCantidad(@RequestParam("productoId") Long productoId,
                                     @RequestParam("cantidad") int cantidad,
                                     HttpSession session) {
        List<Map<String, Object>> cart = getCart(session);
        for (Map<String, Object> item : cart) {
            if (item.get("productoId").equals(productoId)) {
                if (cantidad <= 0) {
                    cart.remove(item);
                } else {
                    item.put("cantidad", cantidad);
                    item.put("subtotal", cantidad * (double) item.get("precio"));
                }
                break;
            }
        }
        return "redirect:/tienda/carrito";
    }

    @PostMapping("/tienda/pagar")
    public String pagar(HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) return "redirect:/login";

        List<Map<String, Object>> cart = getCart(session);
        if (cart.isEmpty()) return "redirect:/tienda/carrito";

        double total = Math.round(cart.stream().mapToDouble(i -> (double) i.get("subtotal")).sum() * 100.0) / 100.0;
        Pedido pedido = new Pedido(user.getEmail(), total);

        for (Map<String, Object> item : cart) {
            DetallePedido detalle = new DetallePedido(
                (String) item.get("nombre"),
                (double) item.get("precio"),
                (int) item.get("cantidad")
            );
            detalle.setPedido(pedido);
            pedido.getDetalles().add(detalle);
        }

        pedidoService.save(pedido);

        // Limpiar carrito
        session.removeAttribute("carrito");

        return "redirect:/tienda/pedidos";
    }

    @GetMapping("/tienda/pedidos")
    public String verPedidos(HttpSession session, Model model) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) return "redirect:/login";

        boolean isAdmin = "admin@gym.com".equalsIgnoreCase(user.getEmail());
        List<Pedido> pedidos;

        if (isAdmin) {
            pedidos = pedidoService.findAll();
        } else {
            pedidos = pedidoService.findByUsuarioEmail(user.getEmail());
        }

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("isAdmin", isAdmin);
        return "pedidos";
    }

    /*
     * Admin: cambiar estado de un pedido
     */
    @PostMapping("/tienda/actualizar-estado")
    public String actualizarEstado(@RequestParam("pedidoId") Long pedidoId,
                                   @RequestParam("estado") String estado,
                                   HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null || !"admin@gym.com".equalsIgnoreCase(user.getEmail())) {
            return "redirect:/login";
        }
        pedidoService.actualizarEstado(pedidoId, estado);
        return "redirect:/tienda/pedidos";
    }

    /*
     * Admin: CRUD de productos
     */
    @GetMapping("/tienda/admin")
    public String adminProductos(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null || !"admin@gym.com".equalsIgnoreCase(user.getEmail())) {
            return "redirect:/login";
        }
        return "redirect:/tienda";
    }

    @PostMapping("/tienda/admin/guardar")
    public String guardarProducto(@RequestParam(value = "id", required = false) Long id,
                                  @RequestParam("nombre") String nombre,
                                  @RequestParam("descripcion") String descripcion,
                                  @RequestParam("precio") double precio,
                                  @RequestParam(value = "imagenUrl", required = false) String imagenUrl,
                                  @RequestParam("stock") int stock,
                                  @RequestParam("categoria") String categoria,
                                  @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
                                  HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null || !"admin@gym.com".equalsIgnoreCase(user.getEmail())) {
            return "redirect:/login";
        }

        Producto prod = (id != null) ? productoService.findById(id).orElse(new Producto()) : new Producto();
        prod.setNombre(nombre);
        prod.setDescripcion(descripcion);
        prod.setPrecio(precio);
        prod.setStock(stock);
        prod.setCategoria(categoria);
        prod.setActivo(true);

        // Si subió un archivo, guardarlo y usar esa ruta
        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                Files.createDirectories(UPLOADS_DIR);
                String ext = "";
                String originalName = imagenFile.getOriginalFilename();
                if (originalName != null && originalName.contains(".")) {
                    ext = originalName.substring(originalName.lastIndexOf("."));
                }
                String filename = UUID.randomUUID().toString() + ext;
                Path destino = UPLOADS_DIR.resolve(filename);
                Files.copy(imagenFile.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
                prod.setImagenUrl("/uploads/" + filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (imagenUrl != null && !imagenUrl.isBlank()) {
            prod.setImagenUrl(imagenUrl);
        }

        productoService.save(prod);
        return "redirect:/tienda/admin";
    }

    @GetMapping("/uploads/{filename}")
    public ResponseEntity<Resource> servirImagen(@PathVariable String filename) {
        try {
            Path file = UPLOADS_DIR.resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            String contentType = Files.probeContentType(file);
            if (contentType == null) contentType = "application/octet-stream";
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=86400")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/tienda/admin/toggle-estado")
    public String toggleEstadoProducto(@RequestParam("id") Long id,
                                        HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null || !"admin@gym.com".equalsIgnoreCase(user.getEmail())) {
            return "redirect:/login";
        }
        productoService.findById(id).ifPresent(p -> {
            p.setActivo(!p.isActivo());
            productoService.save(p);
        });
        return "redirect:/tienda/admin";
    }

    @PostMapping("/tienda/admin/eliminar")
    public String eliminarProducto(@RequestParam("id") Long id,
                                   HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null || !"admin@gym.com".equalsIgnoreCase(user.getEmail())) {
            return "redirect:/login";
        }
        productoService.deleteById(id);
        return "redirect:/tienda/admin";
    }
}
