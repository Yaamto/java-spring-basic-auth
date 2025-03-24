package com.example.demo.repository;

import com.example.demo.model.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<CarModel, Long> {
    // Recherches personnalisées (exemple)
    List<CarModel> findByBrand(String brand);
}