package com.example.usersv.service;

import com.example.usersv.dto.CartDto;
import com.example.usersv.dto.UserdataDto;
import com.example.usersv.entity.EmailRequest;
import com.example.usersv.entity.NewPasswordRequest;
import com.example.usersv.entity.UserLoginRequest;
import com.example.usersv.jwt.JwtUtils;
import com.example.usersv.model.Userdata;
import com.example.usersv.repository.ICartAPI;
import com.example.usersv.repository.IUserdataRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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

    @Autowired
    private JavaMailSender mailSender;

    private static final Logger logger = LoggerFactory.getLogger(UserdataService.class);

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
        Userdata userdata = null;
        try {
            userdata = iUserdataRepository.findUserdataByEmail(loginRequest.getEmail());
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username or password");
        }

        if (!this.passwordEncoder.matches(loginRequest.getPassword(), userdata.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        String token = jwtUtils.generateAccesToken(userdata.getRole(), userdata.getName(), userdata.getIdCart(), userdata.getEmail(), userdata.getDni());
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) Duration.ofMinutes(1440L).toSeconds()); // 1 dia
        response.addCookie(cookie);
        return ResponseEntity.ok(token);
    }

    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
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


    public ResponseEntity<?> forgotPassword(EmailRequest emailRequest) {
        // Genera el token de restablecimiento de contraseña
        String token = UUID.randomUUID().toString();
        logger.info(token);

        try {
            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mail, true);

            helper.setTo(emailRequest.getEmail());
            System.out.println(emailRequest.getEmail());
            logger.info("email guardado");

            helper.setSubject("Restablecimiento de contraseña");
            logger.info("asunto guardado");

            String content = "<html>"
                    + "<body style='font-family: Arial, sans-serif; padding: 20px; background-color: #f6f6f6;'>"
                    + "<div style='max-width: 600px; margin: 0 auto;'>"
                    + "<div style='background-color: #ffffff; border-radius: 10px; padding: 20px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);'>"
                    + "<h1 style='color: #f44336; margin-top: 0;'>Porton Palmas</h1>"
                    + "<p>Para restablecer tu contraseña, haz clic en el siguiente enlace:</p>"
                    + "<a href='http://localhost:8080/reset-password?token=" + token + "' style='display: inline-block; font-weight: bold; background-color: #4CAF50; color: white; padding: 10px 20px; margin: 20px 0; text-decoration: none; border-radius: 5px;'>Restablecer contraseña</a>"
                    + "<p style='color: #888888;'>Si no solicitaste un restablecimiento de contraseña, ignora este correo electrónico.</p>"
                    + "</div>"
                    + "</div>"
                    + "</body>"
                    + "</html>";
            helper.setText(content, true); // El segundo parámetro indica que el contenido es HTML
            logger.info("verificando si email existe...");

            // Envía el correo electrónico
            Userdata userForgotten = iUserdataRepository.findById(emailRequest.getEmail()).orElseThrow(() -> {
                logger.error("Usuario no encontrado");
                return new UsernameNotFoundException("Usuario no encontrado");
            });
            mailSender.send(mail);
            logger.info("email enviado");

            userForgotten.setResetToken(token);
            userForgotten.setExpDateResetToken(java.time.LocalDate.now().plusDays(1));
            iUserdataRepository.save(userForgotten);

        } catch (MessagingException e) {
            logger.error("error al enviar el email");
            return ResponseEntity.badRequest().body("Error al enviar el email");
        }

        return ResponseEntity.ok("Password reset email sent");
    }

    public ResponseEntity<?> resetPassword(String token, NewPasswordRequest newPasswordRequest) {
        Userdata user = iUserdataRepository.findUserdataByResetToken(token);
        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid token");
        }

        if (java.time.LocalDate.now().isAfter(user.getExpDateResetToken())) {
            return ResponseEntity.badRequest().body("Token expired");
        }

        user.setPassword(passwordEncoder.encode(newPasswordRequest.getNewPassword()));
        user.setResetToken(null);
        user.setExpDateResetToken(null);
        iUserdataRepository.save(user);
        return ResponseEntity.ok("Password reset successfully");

    }
}
