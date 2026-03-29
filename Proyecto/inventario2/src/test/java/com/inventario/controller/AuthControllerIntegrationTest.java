package com.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventario.dto.AuthDtos.*;
import com.inventario.model.Usuario;
import com.inventario.repository.UsuarioRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UsuarioRepository usuarioRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        if (!usuarioRepository.existsByEmail("auth@inv.com")) {
            usuarioRepository.save(Usuario.builder()
                    .nombre("Auth User").email("auth@inv.com")
                    .password(passwordEncoder.encode("Auth123!"))
                    .rol(Usuario.Rol.ADMIN).build());
        }
    }

    @Test
    @Order(1)
    @DisplayName("POST /api/auth/login con credenciales válidas retorna 200 y token")
    void login_credencialesValidas_retornaToken() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("auth@inv.com");
        req.setPassword("Auth123!");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("auth@inv.com"))
                .andExpect(jsonPath("$.rol").value("ADMIN"));
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/auth/login con contraseña incorrecta retorna 401")
    void login_passwordIncorrecto_retorna401() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("auth@inv.com");
        req.setPassword("WrongPass!");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/auth/registro con datos válidos retorna 201")
    void registro_datosValidos_retorna201() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setNombre("Nuevo Usuario");
        req.setEmail("nuevo@inv.com");
        req.setPassword("Nuevo123!");
        req.setRol("OPERADOR");

        mockMvc.perform(post("/api/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    @Order(4)
    @DisplayName("POST /api/auth/registro con email duplicado retorna 409")
    void registro_emailDuplicado_retorna409() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setNombre("Duplicado");
        req.setEmail("auth@inv.com");
        req.setPassword("Dup123!");

        mockMvc.perform(post("/api/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @Order(5)
    @DisplayName("POST /api/auth/login con email vacío retorna 400")
    void login_emailVacio_retorna400() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("");
        req.setPassword("Test123!");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}
