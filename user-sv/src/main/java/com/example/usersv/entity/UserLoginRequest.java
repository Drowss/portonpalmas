package com.example.usersv.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginRequest {
    private String email;
    private String password;
}
