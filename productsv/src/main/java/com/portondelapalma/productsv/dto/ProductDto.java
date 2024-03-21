package com.portondelapalma.productsv.dto;

import com.portondelapalma.productsv.model.Category;
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
public class ProductDto {
    private Long idProduct;
    @NotBlank(message = "El campo 'nameProduct' no puede estar en blanco.")
    @Size(min = 1, max = 100, message = "El campo 'nameProduct' debe tener entre 1 y 100 caracteres.")
    private String nameProduct;
    @NotBlank(message = "El campo 'description' no puede estar en blanco.")
    @Size(min = 1, max = 1000, message = "El campo 'description' puede tener hasta 1000 caracteres.")
    private String description;
    @NotNull(message = "El campo 'price' no puede ser nulo.")
    @PositiveOrZero(message = "El campo 'price' debe ser cero o un número positivo.")
    private Long price;
    @NotNull(message = "El campo 'stock' no puede ser nulo.")
    @PositiveOrZero(message = "El campo 'stock' debe ser cero o un número positivo.")
    private Integer stock;
    private String imagePath;
    @Enumerated(EnumType.STRING)
    private Category category;
}
