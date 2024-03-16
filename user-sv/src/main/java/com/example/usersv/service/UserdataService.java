package com.example.usersv.service;

import com.example.usersv.dto.CartDto;
import com.example.usersv.dto.UserdataDto;
import com.example.usersv.entity.UserLoginRequest;
import com.example.usersv.jwt.JwtUtils;
import com.example.usersv.model.Userdata;
import com.example.usersv.repository.ICartAPI;
import com.example.usersv.repository.IUserdataRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;

@Service
public class UserdataService {

    @Autowired
    private IUserdataRepository iUserdataRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ICartAPI iCartAPI;

    public UserdataDto saveUserdata(Userdata userdata) {
        userdata.setRole("USER");
        CartDto cartDto = CartDto.builder()
                .items(new HashMap<>())
                .total(0L)
                .build();
        userdata.setIdCart(iCartAPI.createCart(cartDto));
        userdata.setPassword(this.passwordEncoder.encode(userdata.getPassword()));
        iUserdataRepository.save(userdata);
        return modelMapper.map(userdata, UserdataDto.class);
    }

    public ResponseEntity<?> loginUser(UserLoginRequest loginRequest, HttpServletResponse response) {
        Userdata userdata = iUserdataRepository.findUserdataByEmail(loginRequest.getEmail());
        System.out.println(userdata.getEmail() +" " + userdata.getPassword() + " " + userdata.getRole());

        if (!this.passwordEncoder.matches(loginRequest.getPassword(), userdata.getPassword())) {
            throw new UsernameNotFoundException("Invalid username or password");
        }
        String token = jwtUtils.generateAccesToken(userdata.getRole(), userdata.getName());
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge((int) Duration.ofMinutes(1440L).toSeconds()); // 1 dia
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    public UserdataDto getUserdata(String email) {
        Userdata userdata = iUserdataRepository.findUserdataByEmail(email);
        return modelMapper.map(userdata, UserdataDto.class);
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
