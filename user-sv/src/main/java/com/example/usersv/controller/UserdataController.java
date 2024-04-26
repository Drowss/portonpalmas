package com.example.usersv.controller;

import com.example.usersv.dto.UserdataDto;
import com.example.usersv.entity.EmailRequest;
import com.example.usersv.entity.NewPasswordRequest;
import com.example.usersv.entity.UserLoginRequest;
import com.example.usersv.model.Userdata;
import com.example.usersv.service.UserdataService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user/auth")
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

    @GetMapping
    public List<UserdataDto> getAllUserdata() {
        return userdataService.getAllUserdata();
    }

    @PutMapping("/put")
    public UserdataDto putUserdata(HttpServletRequest request, @RequestBody Userdata userdata) {
        return userdataService.putUserdata(request, userdata);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody EmailRequest emailRequest) {
        return userdataService.forgotPassword(emailRequest);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestBody NewPasswordRequest newPasswordRequest) {
        return userdataService.resetPassword(token, newPasswordRequest);
    }
}
