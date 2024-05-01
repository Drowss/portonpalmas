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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
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
import java.util.Optional;
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

    @PersistenceContext
    EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(UserdataService.class);

    @Transactional
    public void insertWithQuery(Userdata userdata) {
        userdata.setIdCart(iCartAPI.createCart(CartDto.builder().items(new HashMap<>()).total(0L).build()));
        userdata.setRole("USER");

        entityManager.createNativeQuery("INSERT INTO userdata (email, name, password, city, region, street_type, street_number, local_apto_number, postal_code, cellphone, dni, role, id_cart) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .setParameter(1, userdata.getEmail())
                .setParameter(2, userdata.getName())
                .setParameter(3, passwordEncoder.encode(userdata.getPassword()))
                .setParameter(4, userdata.getCity())
                .setParameter(5, userdata.getRegion())
                .setParameter(6, userdata.getStreetType())
                .setParameter(7, userdata.getStreetNumber())
                .setParameter(8, userdata.getLocalAptoNumber())
                .setParameter(9, userdata.getPostalCode())
                .setParameter(10, userdata.getCellphone())
                .setParameter(11, userdata.getDni())
                .setParameter(12, userdata.getRole())
                .setParameter(13, userdata.getIdCart())
                .executeUpdate();
    }

    @Transactional
    public void deleteWithQuery(String email) {
        entityManager.createNativeQuery("DELETE FROM userdata WHERE email = ?")
                .setParameter(1, email)
                .executeUpdate();
    }

    @Transactional
    public void updateWithQuery(Userdata userdata) {
        Userdata user = this.findUserdataByEmailQuery(userdata.getEmail());
        Optional.ofNullable(userdata.getName()).ifPresent(user::setName);
        Optional.ofNullable(userdata.getPassword()).ifPresent(password -> user.setPassword(passwordEncoder.encode(password)));
        Optional.ofNullable(userdata.getCity()).ifPresent(user::setCity);
        Optional.ofNullable(userdata.getRegion()).ifPresent(user::setRegion);
        Optional.ofNullable(userdata.getStreetType()).ifPresent(user::setStreetType);
        Optional.ofNullable(userdata.getStreetNumber()).ifPresent(user::setStreetNumber);
        Optional.ofNullable(userdata.getLocalAptoNumber()).ifPresent(user::setLocalAptoNumber);
        Optional.ofNullable(userdata.getPostalCode()).ifPresent(user::setPostalCode);
        Optional.ofNullable(userdata.getCellphone()).ifPresent(user::setCellphone);
        Optional.ofNullable(userdata.getDni()).ifPresent(user::setDni);
        Optional.ofNullable(userdata.getRole()).ifPresent(user::setRole);
        Optional.ofNullable(userdata.getIdCart()).ifPresent(user::setIdCart);

        entityManager.createNativeQuery("UPDATE userdata SET name = ?, password = ?, city = ?, region = ?, street_type = ?, street_number = ?, local_apto_number = ?, postal_code = ?, cellphone = ?, dni = ?, role = ?, id_cart = ? WHERE email = ?")
                .setParameter(1, user.getName())
                .setParameter(2, user.getPassword())
                .setParameter(3, user.getCity())
                .setParameter(4, user.getRegion())
                .setParameter(5, user.getStreetType())
                .setParameter(6, user.getStreetNumber())
                .setParameter(7, user.getLocalAptoNumber())
                .setParameter(8, user.getPostalCode())
                .setParameter(9, user.getCellphone())
                .setParameter(10, user.getDni())
                .setParameter(11, user.getRole())
                .setParameter(12, user.getIdCart())
                .setParameter(13, user.getEmail())
                .executeUpdate();
    }

    @Transactional
    public List<Userdata> selectWithQuery() {
        return entityManager.createNativeQuery("SELECT * FROM userdata", Userdata.class).getResultList();
    }

    @Transactional
    public Userdata findUserdataByEmailQuery(String email) {
        return (Userdata) entityManager.createNativeQuery("SELECT * FROM userdata WHERE email = ?", Userdata.class)
                .setParameter(1, email)
                .getSingleResult();
    }

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
        iUserdataRepository.deleteUserdataORM(email);
    }

    public List<UserdataDto> getAllUserdata() {
        return iUserdataRepository.findAllUserdataORM().stream()
                .map(userdata ->
                        modelMapper.map(userdata, UserdataDto.class)).toList();
    }

    public UserdataDto putUserdata(HttpServletRequest request, Userdata userdata) {

        if (request.getCookies() == null) {
            throw new RuntimeException("No token found");
        }

        List<Cookie> list = List.of(request.getCookies());

        String token = list.stream()
                .filter(cookie -> cookie.getName().equals("token"))
                .findFirst().get().getValue();

        if (jwtUtils.isExpired(token)) {
            throw new RuntimeException("Token expired");
        }

        Userdata user = iUserdataRepository.findById(jwtUtils.getUserEmailFromRequest(token)).orElseThrow(() -> {
            logger.error("Usuario no encontrado");
            return new UsernameNotFoundException("User not found");
        });

        Optional.ofNullable(userdata.getName()).ifPresent(user::setName);
        Optional.ofNullable(userdata.getCellphone()).ifPresent(user::setCellphone);
        Optional.ofNullable(userdata.getDni()).ifPresent(user::setDni);
        Optional.ofNullable(userdata.getPassword()).ifPresent(password -> user.setPassword(passwordEncoder.encode(password)));
        Optional.ofNullable(userdata.getEmail()).ifPresent(user::setEmail);
        iUserdataRepository.save(user);

        return modelMapper.map(user, UserdataDto.class);
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
                    + "<a href='http://localhost:4200/reset-password?token=" + token + "' style='display: inline-block; font-weight: bold; background-color: #4CAF50; color: white; padding: 10px 20px; margin: 20px 0; text-decoration: none; border-radius: 5px;'>Restablecer contraseña</a>"
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
                return new UsernameNotFoundException("User not found");
            });
            mailSender.send(mail);
            logger.info("email enviado");

            userForgotten.setResetToken(token);
            userForgotten.setExpDateResetToken(java.time.LocalDate.now().plusDays(1));
            iUserdataRepository.save(userForgotten);

        } catch (MessagingException e) {
            logger.error("error al enviar el email");
            return ResponseEntity.badRequest().body("Error while sending email");
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
