package com.portondelapalma.productsv.model;

import com.portondelapalma.productsv.dto.ProductDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProduct;
    private String nameProduct;
    private String description;
    private Long price;
    private Integer stock;
    private String imagePath;
    private Category category;

    public ProductDto toDto() {
        return ProductDto.builder()
                .nameProduct(nameProduct)
                .description(description)
                .price(price)
                .stock(stock)
                .imagePath(imagePath)
                .category(category)
                .build();
    }
}
