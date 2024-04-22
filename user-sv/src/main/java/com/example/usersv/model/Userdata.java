package com.example.usersv.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class Userdata {

    @Id
    @Email(message = "El correo electrónico debe ser válido")
    @NotBlank(message = "El correo electrónico es obligatorio")
    private String email;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotBlank(message = "La dirección es obligatoria")
    private String address;

    @NotBlank(message = "El número de teléfono es obligatorio")
    private String cellphone;
    @Column(unique = true)
    @NotNull(message = "El DNI es obligatorio")
    private String dni;
    private String role;
    private Long idCart;
    private String resetToken;
    private LocalDate expDateResetToken;
}
