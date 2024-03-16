package com.example.usersv.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @NotBlank(message = "El rol es obligatorio")
    private String role;
    private Long idCart;
}
