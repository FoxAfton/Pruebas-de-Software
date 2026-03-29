package com.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventario.dto.InventarioDtos.ProductoRequest;
import com.inventario.model.Usuario;
import com.inventario.repository.UsuarioRepository;
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
class ProductoControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UsuarioRepository usuarioRepository;
    @Autowired JwtUtils jwtUtils;
    @Autowired PasswordEncoder passwordEncoder;

    private String token;

    @BeforeEach
    void setUp() {
        if (!usuarioRepository.existsByEmail("prod@inv.com")) {
            usuarioRepository.save(Usuario.builder()
                    .nombre("Prod").email("prod@inv.com")
                    .password(passwordEncoder.encode("Test123!"))
                    .rol(Usuario.Rol.ADMIN).build());
        }
        token = jwtUtils.generateToken("prod@inv.com");
    }

    @Test
    @Order(1)
    @DisplayName("POST /api/productos con datos válidos retorna 201")
    void crearProducto_datosValidos_retorna201() throws Exception {
        ProductoRequest req = new ProductoRequest();
        req.setNombre("Teclado Gamer"); req.setPrecio(450.0); req.setStock(15);

        mockMvc.perform(post("/api/productos")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Teclado Gamer"))
                .andExpect(jsonPath("$.activo").value(true));
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/productos con precio negativo retorna 400")
    void crearProducto_precioNegativo_retorna400() throws Exception {
        ProductoRequest req = new ProductoRequest();
        req.setNombre("Producto Inválido"); req.setPrecio(-10.0); req.setStock(5);

        mockMvc.perform(post("/api/productos")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/productos retorna página con status 200")
    void listarProductos_retornaPaginado200() throws Exception {
        mockMvc.perform(get("/api/productos?page=0&size=10")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/productos/{id} inexistente retorna 404")
    void obtenerProducto_idInexistente_retorna404() throws Exception {
        mockMvc.perform(get("/api/productos/99999")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    @DisplayName("GET /api/productos/stock-bajo retorna lista correcta")
    void stockBajo_retornaLista200() throws Exception {
        mockMvc.perform(get("/api/productos/stock-bajo?umbral=100")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
