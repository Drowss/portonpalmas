package com.drow.salesv.model;

import com.drow.salesv.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleInf {

    private Long id;
    private Long total;
    private String userEmail;
    private String dni;
    private List<ProductDto> items;
    private LocalDate date;
}
