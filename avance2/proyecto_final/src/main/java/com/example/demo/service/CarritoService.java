package com.example.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.CarritoItem;
import com.example.demo.model.Producto;
import com.example.demo.repository.CarritoItemRepository;

@Service
public class CarritoService {

    @Autowired
    private CarritoItemRepository carritoItemRepository;

    @Autowired
    private ProductoService productoService;

    public List<Map<String, Object>> getCart(String usuarioEmail) {
        List<CarritoItem> items = carritoItemRepository.findByUsuarioEmailOrderByIdAsc(usuarioEmail);
        List<Map<String, Object>> cart = new ArrayList<>();
        for (CarritoItem ci : items) {
            Producto prod = productoService.findById(ci.getProductoId()).orElse(null);
            if (prod == null) continue;
            Map<String, Object> item = new HashMap<>();
            item.put("cartItemId", ci.getId());
            item.put("productoId", prod.getId());
            item.put("nombre", prod.getNombre());
            item.put("precio", prod.getPrecio());
            item.put("imagenUrl", prod.getImagenUrl());
            item.put("cantidad", ci.getCantidad());
            item.put("subtotal", prod.getPrecio() * ci.getCantidad());
            cart.add(item);
        }
        return cart;
    }

    public int getCartCount(String usuarioEmail) {
        return carritoItemRepository.countByUsuarioEmail(usuarioEmail);
    }

    public void addItem(String usuarioEmail, Long productoId, int cantidad) {
        Producto prod = productoService.findById(productoId).orElse(null);
        if (prod == null || !prod.isActivo() || prod.getStock() <= 0) return;

        var existing = carritoItemRepository.findByUsuarioEmailAndProductoId(usuarioEmail, productoId);
        if (existing.isPresent()) {
            CarritoItem ci = existing.get();
            int nuevaCant = ci.getCantidad() + cantidad;
            ci.setCantidad(Math.min(nuevaCant, prod.getStock()));
            carritoItemRepository.save(ci);
        } else {
            carritoItemRepository.save(new CarritoItem(usuarioEmail, productoId, Math.min(cantidad, prod.getStock())));
        }
    }

    public void updateQuantity(String usuarioEmail, Long productoId, int cantidad) {
        var existing = carritoItemRepository.findByUsuarioEmailAndProductoId(usuarioEmail, productoId);
        if (existing.isPresent()) {
            if (cantidad <= 0) {
                carritoItemRepository.delete(existing.get());
            } else {
                CarritoItem ci = existing.get();
                ci.setCantidad(cantidad);
                carritoItemRepository.save(ci);
            }
        }
    }

    public void removeItem(String usuarioEmail, Long productoId) {
        var existing = carritoItemRepository.findByUsuarioEmailAndProductoId(usuarioEmail, productoId);
        existing.ifPresent(carritoItemRepository::delete);
    }

        @Transactional
public void clearCart(String usuarioEmail) {
    carritoItemRepository.deleteByUsuarioEmail(usuarioEmail);
}
}
