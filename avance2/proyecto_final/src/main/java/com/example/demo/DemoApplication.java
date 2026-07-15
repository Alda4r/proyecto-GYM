package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@SpringBootApplication
@EnableScheduling
public class DemoApplication implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Autocura: Asegura que la columna exista en PostgreSQL
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE pedidos ADD COLUMN IF NOT EXISTS codigo_reclamacion VARCHAR(255)");
        } catch (Exception e) {
            System.err.println("Error al intentar actualizar esquema: " + e.getMessage());
        }
    }
}
