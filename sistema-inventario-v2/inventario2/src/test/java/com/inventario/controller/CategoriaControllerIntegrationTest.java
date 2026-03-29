package com.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventario.model.Categoria;
import com.inventario.repository.CategoriaRepository;
import com.inventario.repository.UsuarioRepository;
import com.inventario.model.Usuario;
import com.inventario.security.JwtUtils;
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
class CategoriaControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired CategoriaRepository categoriaRepository;
    @Autowired UsuarioRepository usuarioRepository;
    @Autowired JwtUtils jwtUtils;
    @Autowired PasswordEncoder passwordEncoder;

    private String token;

    @BeforeEach
    void setUp() {
        if (!usuarioRepository.existsByEmail("test@inv.com")) {
            usuarioRepository.save(Usuario.builder()
                    .nombre("Test").email("test@inv.com")
                    .password(passwordEncoder.encode("Test123!"))
                    .rol(Usuario.Rol.ADMIN).build());
        }
        token = jwtUtils.generateToken("test@inv.com");
    }

    @Test
    @Order(1)
    @DisplayName("POST /api/categorias con datos válidos retorna 201")
    void crearCategoria_datosValidos_retorna201() throws Exception {
        Categoria cat = Categoria.builder().nombre("Electronica-Test").descripcion("Desc").build();

        mockMvc.perform(post("/api/categorias")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cat)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Electronica-Test"));
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/categorias con nombre duplicado retorna 409")
    void crearCategoria_nombreDuplicado_retorna409() throws Exception {
        Categoria cat = Categoria.builder().nombre("Electronica-Test").build();

        mockMvc.perform(post("/api/categorias")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cat)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/categorias retorna lista con status 200")
    void listarCategorias_retorna200() throws Exception {
        mockMvc.perform(get("/api/categorias")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/categorias/{id} inexistente retorna 404")
    void obtenerCategoria_idInexistente_retorna404() throws Exception {
        mockMvc.perform(get("/api/categorias/99999")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    @DisplayName("Acceso sin token retorna 401")
    void acceso_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isUnauthorized());
    }
}
