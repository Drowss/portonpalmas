package com.portondelapalma.CartService.model;

import com.portondelapalma.CartService.dto.CartDto;
import com.portondelapalma.CartService.dto.ProductDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    private Map<String, Integer> items;
    private Long total;

    public CartDto toDto() {
        return CartDto.builder()
                .idCart(id)
                .items(items)
                .total(total)
                .build();
    }
}
