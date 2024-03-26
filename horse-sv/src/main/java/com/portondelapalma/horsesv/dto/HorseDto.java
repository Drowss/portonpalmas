package com.portondelapalma.horsesv.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorseDto {

    private Long idHorse;
    private String breed;
    private String description;
    private Long price;
    private String imagePath;
    private LocalDate bornOn;
}
