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
    @Column(nullable = false, length = 100)
    private String nameProduct;
    @Column(nullable = false, length = 1000)
    private String description;
    @Column(nullable = false)
    private Long price;
    @Column(nullable = false, columnDefinition = "int check(stock >= 0)")
    private Integer stock;
    @Column(nullable = false)
    private String imagePath;
    private Category category;

    public ProductDto toDto() {
        return ProductDto.builder()
                .idProduct(idProduct)
                .nameProduct(nameProduct)
                .description(description)
                .price(price)
                .stock(stock)
                .imagePath(imagePath)
                .category(category)
                .build();
    }
}
