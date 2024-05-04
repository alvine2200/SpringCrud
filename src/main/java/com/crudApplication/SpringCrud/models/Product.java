package com.crudApplication.SpringCrud.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, name = "id")
    private Long id;
    private String name;
    private String brand;
    private String category;
    private double price;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Date createdAt;
    private String imageFileName;

}
