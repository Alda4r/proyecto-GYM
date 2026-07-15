package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.DetallePedido;
import com.example.demo.model.Pedido;
import com.example.demo.repository.PedidoRepository;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public List<Pedido> findByUsuarioEmail(String email) {
        return pedidoRepository.findByUsuarioEmailOrderByFechaHoraDesc(email);
    }

    public List<Pedido> findAll() {
        return pedidoRepository.findAllByOrderByFechaHoraDesc();
    }

    public Pedido save(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public void actualizarEstado(Long id, String nuevoEstado) {
        pedidoRepository.findById(id).ifPresent(p -> {
            p.setEstado(nuevoEstado);
            pedidoRepository.save(p);
        });
    }
}
