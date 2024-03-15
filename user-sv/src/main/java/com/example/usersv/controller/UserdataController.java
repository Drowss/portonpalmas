package com.example.usersv.controller;

import com.example.usersv.entity.UserLoginRequest;
import com.example.usersv.model.Userdata;
import com.example.usersv.service.UserdataService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public void register(@RequestBody Userdata userdata) {
        userdataService.saveUserdata(userdata);
    }
}
