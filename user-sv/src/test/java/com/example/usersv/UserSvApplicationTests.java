package com.example.usersv;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.usersv.dto.CartDto;
import com.example.usersv.dto.UserdataDto;
import com.example.usersv.entity.UserLoginRequest;
import com.example.usersv.jwt.JwtUtils;
import com.example.usersv.model.Userdata;
import com.example.usersv.repository.ICartAPI;
import com.example.usersv.repository.IUserdataRepository;
import com.example.usersv.service.UserdataService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class UserSvApplicationTests {

	@Mock
	private IUserdataRepository iUserdataRepository;

	@Mock
	private ICartAPI iCartAPI;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtUtils jwtUtils;

	@Mock
	private ModelMapper modelMapper;

	@Mock
	private HttpServletResponse response;

	@InjectMocks
	private UserdataService userService;

	private UserLoginRequest loginRequest;
	private Userdata userdata;
	private UserdataDto userdataDto;
	@Mock
	private Cookie cookie;
	@Mock
	private HttpServletRequest request;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		loginRequest = UserLoginRequest.builder()
				.email("test@example.com")
				.password("password")
				.build();

		userdata = Userdata.builder()
				.email("test@example.com")
				.password("encodedPassword")
				.role("USER")
				.name("Test User")
				.idCart(1L)
				.dni("12345678")
				.build();

		userdataDto = UserdataDto.builder()
				.email("test@example.com")
				.name("Test User")
				.address("123 Test St")
				.cellphone("1234567890")
				.dni("123456789")
				.build();
	}

	@Test
	public void loginUser_ValidCredentials_ReturnsToken() throws Exception {
		when(iUserdataRepository.findUserdataByEmail(loginRequest.getEmail())).thenReturn(userdata);
		when(passwordEncoder.matches(loginRequest.getPassword(), userdata.getPassword())).thenReturn(true);
		when(jwtUtils.generateAccesToken(userdata.getRole(), userdata.getName(), userdata.getIdCart(), userdata.getEmail(), userdata.getDni()))
				.thenReturn("generatedToken");

		ResponseEntity<?> responseEntity = userService.loginUser(loginRequest, response);

		verify(response).addCookie(any(Cookie.class)); // Verifica que se añadió la cookie
		assertEquals("generatedToken", responseEntity.getBody());
	}

	@Test
	public void loginUser_InvalidEmail_ThrowsBadCredentialsException() {
		when(iUserdataRepository.findUserdataByEmail(loginRequest.getEmail())).thenThrow(new RuntimeException());

		assertThrows(BadCredentialsException.class, () -> {
			userService.loginUser(loginRequest, response);
		});
	}

	@Test
	public void loginUser_InvalidPassword_ThrowsBadCredentialsException() {
		when(iUserdataRepository.findUserdataByEmail(loginRequest.getEmail())).thenReturn(userdata);
		when(passwordEncoder.matches(loginRequest.getPassword(), userdata.getPassword())).thenReturn(false);

		assertThrows(BadCredentialsException.class, () -> {
			userService.loginUser(loginRequest, response);
		});
	}

	@Test
	public void saveUserdata_Success_ReturnsUserdataDto() {
		when(iCartAPI.createCart(any(CartDto.class))).thenReturn(1L);
		when(passwordEncoder.encode(userdata.getPassword())).thenReturn("encodedPassword");
		when(modelMapper.map(userdata, UserdataDto.class)).thenReturn(new UserdataDto());

		UserdataDto result = userService.saveUserdata(userdata);

		assertNotNull(result);
	}

	@Test
	public void testPutUserdata_UserNotFound() {
		Cookie cookie = new Cookie("token", "validToken");
		when(request.getCookies()).thenReturn(new Cookie[]{cookie});
		when(jwtUtils.isExpired("validToken")).thenReturn(false);
		when(jwtUtils.getUserEmailFromRequest("validToken")).thenReturn("test@example.com");
		when(iUserdataRepository.findById("test@example.com")).thenReturn(Optional.empty());

		UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
			userService.putUserdata(request, userdata);
		});

		assertEquals("User not found", exception.getMessage());
	}

	@Test
	public void testPutUserdata_Success() {
		Cookie cookie = new Cookie("token", "validToken");
		when(request.getCookies()).thenReturn(new Cookie[]{cookie});
		when(jwtUtils.isExpired("validToken")).thenReturn(false);
		when(jwtUtils.getUserEmailFromRequest("validToken")).thenReturn("test@example.com");
		when(iUserdataRepository.findById("test@example.com")).thenReturn(Optional.of(userdata));
		when(modelMapper.map(userdata, UserdataDto.class)).thenReturn(userdataDto);

		UserdataDto result = userService.putUserdata(request, userdata);

		assertEquals(userdataDto, result);
		verify(iUserdataRepository).save(userdata);
	}

}
