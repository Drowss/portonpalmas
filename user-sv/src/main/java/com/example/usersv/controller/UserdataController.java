package com.example.usersv.controller;

import com.example.usersv.dto.UserdataDto;
import com.example.usersv.entity.UserLoginRequest;
import com.example.usersv.model.Userdata;
import com.example.usersv.service.UserdataService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserdataController {

    @Autowired
    private UserdataService userdataService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest loginRequest, HttpServletResponse response) {
        return userdataService.loginUser(loginRequest, response);
    }

    @PostMapping("/register")
    public UserdataDto register(@Valid @RequestBody Userdata userdata) {
        return userdataService.saveUserdata(userdata);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        return userdataService.logout(response);
    }
}
