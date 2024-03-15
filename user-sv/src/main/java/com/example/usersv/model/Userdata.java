package com.example.usersv.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    private String email;
    private String name;
    private String password;
    private String address;
    private String cellphone;
    private String role;
    private Long idCart;
}
