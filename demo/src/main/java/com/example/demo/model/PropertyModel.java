package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private PropertyType propertyType;

    private PropertyTransaction propertyTransaction;

    private String title;

    private String description;

    private Long price;

    private Long surface;

    private Long rooms;

    private Long bathrooms;

    private String city;

    private Long postalCode;

    private String address;

    private Float latitude;

    private Float longitude;

    @JsonProperty("isGarage")
    @ColumnDefault("false")
    private boolean isGarage;

    @JsonProperty("isGarden")
    @ColumnDefault("false")
    private boolean isGarden;

    @JsonProperty("isSwimmingPool")
    @ColumnDefault("false")
    private boolean isSwimmingPool;

    @JsonProperty("isElevator")
    @ColumnDefault("false")
    private boolean isElevator;

    @JsonProperty("isBalcony")
    @ColumnDefault("false")
    private boolean isBalcony;

    @JsonProperty("isMeubled")
    @ColumnDefault("false")
    private boolean isMeubled;

    @JsonProperty("isAirConditioned")
    @ColumnDefault("false")
    private boolean isAirConditioned;

    @JsonProperty("isFibred")
    @ColumnDefault("false")
    private boolean isFibred;

    @ManyToOne
    @JoinColumn(name = "owner_id") // <- personnalise le nom de la colonne FK
    private UserModel owner;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropertyImage> images = new ArrayList<>();
}
