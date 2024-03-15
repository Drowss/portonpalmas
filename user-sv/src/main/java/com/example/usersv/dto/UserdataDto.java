package com.example.usersv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserdataDto {

    private String email;
    private String name;
    private String address;
    private String cellphone;
    private Long idCart;
}
