package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioEmailOrderByFechaHoraDesc(String usuarioEmail);
    List<Pedido> findAllByOrderByFechaHoraDesc();
}
