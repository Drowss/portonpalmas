package com.example.usersv.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class CartDto {
    private Map<String, Integer> items;
    private Long total;
}
