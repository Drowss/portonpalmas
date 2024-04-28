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
    @NotBlank(message = "La ciudad es obligatoria")
    private String city;
    @NotBlank(message = "La región es obligatoria")
    private String region;
    @NotBlank(message = "El tipo de vía")
    private String streetType;
    @NotBlank(message = "El número de la calle es obligatorio")
    private String streetNumber;
    @NotBlank(message = "El número del apartamento es obligatorio")
    private String localAptoNumber;
    @NotBlank(message = "El código postal es obligatorio")
    private String postalCode;


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
