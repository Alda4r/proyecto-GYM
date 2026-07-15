package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.CarritoItem;

public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
    List<CarritoItem> findByUsuarioEmailOrderByIdAsc(String usuarioEmail);
    Optional<CarritoItem> findByUsuarioEmailAndProductoId(String usuarioEmail, Long productoId);
    void deleteByUsuarioEmail(String usuarioEmail);
    int countByUsuarioEmail(String usuarioEmail);
}
