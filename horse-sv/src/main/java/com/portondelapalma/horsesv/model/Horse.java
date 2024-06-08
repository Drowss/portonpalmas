package com.portondelapalma.horsesv.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Horse {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idHorse;

    @NotBlank(message = "El campo 'breed' no puede estar en blanco.")
    @Size(min = 1, max = 100, message = "El campo 'breed' debe tener entre 1 y 100 caracteres.")
    @Column(nullable = false, length = 100)
    private String breed;

    @NotBlank(message = "El campo 'description' no puede estar en blanco.")
    @Size(min = 1, max = 1000, message = "El campo 'description' puede tener hasta 1000 caracteres.")
    @Column(nullable = false, length = 1000)
    private String description;

    @NotNull(message = "El campo 'price' no puede ser nulo.")
    @PositiveOrZero(message = "El campo 'price' debe ser cero o un número positivo.")
    @Column(nullable = false, columnDefinition = "bigint check(price >= 0)")
    private Long price;

    private String imagePath;

    @Past(message = "El campo 'bornOn' debe ser una fecha en el pasado.")
    @NotNull(message = "El campo 'bornOn' no puede ser nulo.")
    @Column(nullable = false)
    private LocalDate bornOn;
}
