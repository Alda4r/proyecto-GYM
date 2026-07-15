package com.example.demo.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.demo.service.CarritoService;
import com.example.demo.service.PedidoService;
import com.example.demo.service.ProductoService;

import jakarta.servlet.http.HttpSession;

@Controller
public class TiendaController {

    private static final Logger log = LoggerFactory.getLogger(TiendaController.class);

    @Autowired
    private ProductoService productoService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private CarritoService carritoService;

    private String getEmail(HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        return (user != null) ? user.getEmail() : null;
    }

    private void actualizarCartCount(HttpSession session, String email) {
        if (email != null) {
            session.setAttribute("carritoCount", carritoService.getCartCount(email));
        }
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
        String email = getEmail(session);
        if (email == null) return "redirect:/login";
        if (email.equalsIgnoreCase("admin@gym.com")) return "redirect:/tienda";

        carritoService.addItem(email, productoId, cantidad);
        actualizarCartCount(session, email);
        return "redirect:/tienda/carrito";
    }

    @GetMapping("/tienda/carrito")
    public String verCarrito(Model model, HttpSession session) {
        String email = getEmail(session);
        if (email == null) return "redirect:/login";
        if (email.equalsIgnoreCase("admin@gym.com")) return "redirect:/tienda";

        List<Map<String, Object>> cart = carritoService.getCart(email);
        double total = cart.stream().mapToDouble(i -> (double) i.get("subtotal")).sum();
        model.addAttribute("carrito", cart);
        model.addAttribute("total", Math.round(total * 100.0) / 100.0);
        actualizarCartCount(session, email);
        return "carrito";
    }

    @PostMapping("/tienda/eliminar")
    public String eliminarDelCarrito(@RequestParam("productoId") Long productoId,
                                     HttpSession session) {
        String email = getEmail(session);
        if (email == null) return "redirect:/login";

        carritoService.removeItem(email, productoId);
        actualizarCartCount(session, email);
        return "redirect:/tienda/carrito";
    }

    @PostMapping("/tienda/actualizar")
    public String actualizarCantidad(@RequestParam("productoId") Long productoId,
                                     @RequestParam("cantidad") int cantidad,
                                     HttpSession session) {
        String email = getEmail(session);
        if (email == null) return "redirect:/login";

        carritoService.updateQuantity(email, productoId, cantidad);
        actualizarCartCount(session, email);
        return "redirect:/tienda/carrito";
    }

    @GetMapping("/tienda/checkout")
    public String verCheckout(Model model, HttpSession session) {
        String email = getEmail(session);
        if (email == null) return "redirect:/login";
        if (email.equalsIgnoreCase("admin@gym.com")) return "redirect:/tienda";

        List<Map<String, Object>> cart = carritoService.getCart(email);
        if (cart.isEmpty()) return "redirect:/tienda/carrito";

        double total = cart.stream().mapToDouble(i -> (double) i.get("subtotal")).sum();
        model.addAttribute("carrito", cart);
        model.addAttribute("total", Math.round(total * 100.0) / 100.0);
        return "pago";
    }

    @PostMapping("/tienda/procesar-pago")
    public String procesarPago(@RequestParam("cardNumber") String cardNumber,
                                @RequestParam("cardName") String cardName,
                                @RequestParam("cardExpiry") String cardExpiry,
                                @RequestParam("cardCvv") String cardCvv,
                                HttpSession session) {
        String email = getEmail(session);
        if (email == null) return "redirect:/login";

        // Simulación: validación básica
        if (cardNumber == null || cardNumber.replace(" ", "").length() < 13 ||
            cardName == null || cardName.isBlank() ||
            cardExpiry == null || !cardExpiry.matches("\\d{2}/\\d{2}") ||
            cardCvv == null || cardCvv.length() < 3) {
            return "redirect:/tienda/checkout?error=datos";
        }

        List<Map<String, Object>> cart = carritoService.getCart(email);
        if (cart.isEmpty()) return "redirect:/tienda/carrito";

        double total = Math.round(cart.stream().mapToDouble(i -> (double) i.get("subtotal")).sum() * 100.0) / 100.0;
        Pedido pedido = new Pedido(email, total);

        try {
            for (Map<String, Object> item : cart) {
                DetallePedido detalle = new DetallePedido(
                    (String) item.get("nombre"),
                    (double) item.get("precio"),
                    (int) item.get("cantidad")
                );
                detalle.setPedido(pedido);
                pedido.getDetalles().add(detalle);

                Long productoId = ((Number) item.get("productoId")).longValue();
                productoService.findById(productoId).ifPresent(prod -> {
                    int cantidad = (int) item.get("cantidad");
                    prod.setStock(Math.max(0, prod.getStock() - cantidad));
                    productoService.save(prod);
                });
            }

            pedidoService.save(pedido);
            session.setAttribute("mensajePago", "Compra simulada registrada correctamente.");
        } catch (Exception ex) {
            log.error("No se pudo registrar el pedido simulado", ex);
            session.setAttribute("mensajePago", "La compra simulada fue aceptada y el carrito se limpió.");
        }

        carritoService.clearCart(email);
        actualizarCartCount(session, email);

        return "redirect:/tienda/pedidos?pagado=ok";
    }

    // --- Pedidos ---

    @GetMapping("/tienda/pedidos")
    public String verPedidos(HttpSession session, Model model) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) return "redirect:/login";

        boolean isAdmin = "admin@gym.com".equalsIgnoreCase(user.getEmail());
        List<Pedido> pedidos = isAdmin ? pedidoService.findAll() : pedidoService.findByUsuarioEmail(user.getEmail());
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("isAdmin", isAdmin);
        return "pedidos";
    }

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

    // --- Admin CRUD de productos ---

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

        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                prod.setFoto(imagenFile.getBytes());
                prod.setFotoMimeType(imagenFile.getContentType());
                prod.setImagenUrl(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (imagenUrl != null && !imagenUrl.isBlank()) {
            prod.setFoto(null);
            prod.setFotoMimeType(null);
            prod.setImagenUrl(imagenUrl);
        }

        Producto saved = productoService.save(prod);

        if (imagenFile != null && !imagenFile.isEmpty()) {
            saved.setImagenUrl("/imagen/producto/" + saved.getId());
            productoService.save(saved);
        }

        return "redirect:/tienda";
    }

    @GetMapping("/imagen/producto/{id}")
    public ResponseEntity<byte[]> servirImagenProducto(@PathVariable Long id) {
        Producto prod = productoService.findById(id).orElse(null);
        if (prod == null || prod.getFoto() == null) {
            return ResponseEntity.notFound().build();
        }
        String mime = prod.getFotoMimeType() != null ? prod.getFotoMimeType() : "image/png";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mime))
                .header(HttpHeaders.CACHE_CONTROL, "max-age=86400")
                .body(prod.getFoto());
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
        return "redirect:/tienda";
    }

    @PostMapping("/tienda/admin/eliminar")
    public String eliminarProducto(@RequestParam("id") Long id,
                                    HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null || !"admin@gym.com".equalsIgnoreCase(user.getEmail())) {
            return "redirect:/login";
        }
        productoService.deleteById(id);
        return "redirect:/tienda";
    }
}
