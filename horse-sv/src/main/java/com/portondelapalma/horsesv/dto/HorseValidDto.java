package com.portondelapalma.horsesv.dto;

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
public class HorseValidDto {

    @NotBlank(message = "")
    @Size(min = 1, max = 100, message = "La raza debe ser entre 1 y 100")
    private String breed;
    @NotBlank
    @Size(min = 1, max = 1000, message = "La descripción puede ser hasta 1000 carácteres de largo")
    private String description;
    @PositiveOrZero(message = "El valor debe ser cero o mayor")
    private Long price;
    private String imagePath;
    @Past
    @NotNull
    private LocalDate bornOn;
}
