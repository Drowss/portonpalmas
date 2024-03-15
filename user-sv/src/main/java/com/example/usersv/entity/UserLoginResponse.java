package com.example.usersv.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginResponse {
    private String token;
}
