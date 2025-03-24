package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
public class CarModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String brand;
    private String model;
}