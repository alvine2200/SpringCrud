package com.crudApplication.SpringCrud.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProductRequest {

    @NotEmpty(message = "Product name is required!")
    private String name;
    @NotEmpty(message = "Product brand is required!")
    private String brand;
    @NotEmpty(message = "Category is required!")
    private String category;
    @NotEmpty(message = "Product price is required!")
    @Min(0)
    private double price;
    @Size(min=10, message = "Description text provided should not be below 10 words")
    @Size(max = 1000, message = "Maximum description words should not be above 1000 words")
    private MultipartFile imageFileName;
}
