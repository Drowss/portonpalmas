package com.drow.salesv.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {
    private Long idProduct;
    private String nameProduct;
    private String description;
    private Long price;
    private Integer stock;
    private String imagePath;
    private Category category;
}
