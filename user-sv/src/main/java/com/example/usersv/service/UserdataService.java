package com.example.usersv.service;

import com.example.usersv.entity.UserLoginRequest;
import com.example.usersv.jwt.JwtUtils;
import com.example.usersv.model.Userdata;
import com.example.usersv.repository.IUserdataRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserdataService {

    @Autowired
    private IUserdataRepository iUserdataRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveUserdata(Userdata userdata) {
        userdata.setRole("USER");
        userdata.setPassword(passwordEncoder.encode(userdata.getPassword()));
        iUserdataRepository.save(userdata);
    }

    public ResponseEntity<?> loginUser(UserLoginRequest loginRequest, HttpServletResponse response) {
        Userdata userdata = iUserdataRepository.findUserdataByEmail(loginRequest.getEmail());

        if (passwordEncoder.matches(loginRequest.getPassword(), userdata.getPassword())) {
            throw new UsernameNotFoundException("Invalid username or password");
        }
        String token = jwtUtils.generateAccesToken(userdata.getRole(), userdata.getName());
        response.setHeader("Authorization", "Bearer " + token);
        return ResponseEntity.ok().build();
    }

    public Userdata getUserdata(String email) {
        return iUserdataRepository.findUserdataByEmail(email);
    }

    public void deleteUserdata(String email) {
        iUserdataRepository.deleteById(email);
    }

    public void updateUserdata(Userdata userdata) {

    }

    public List<Userdata> getAllUserdata() {
        return iUserdataRepository.findAll();
    }
}
