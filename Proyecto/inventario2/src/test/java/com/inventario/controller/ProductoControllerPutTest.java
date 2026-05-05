package com.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventario.dto.InventarioDtos.ProductoRequest;
import com.inventario.model.Categoria;
import com.inventario.model.Proveedor;
import com.inventario.model.Producto;
import com.inventario.model.Usuario;
import com.inventario.repository.CategoriaRepository;
import com.inventario.repository.ProductoRepository;
import com.inventario.repository.ProveedorRepository;
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

/**
 * Pruebas de integración para PUT /api/productos/{id}
 * Tipo: Caja negra | Nivel: Sistema
 * Herramienta: JUnit 5 + Spring Boot Test
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductoControllerPutTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UsuarioRepository usuarioRepository;
    @Autowired ProductoRepository productoRepository;
    @Autowired CategoriaRepository categoriaRepository;
    @Autowired ProveedorRepository proveedorRepository;
    @Autowired JwtUtils jwtUtils;
    @Autowired PasswordEncoder passwordEncoder;

    private String token;
    private Long productoId;
    private Long categoriaId;
    private Long proveedorId;

    @BeforeEach
    void setUp() {
        // Usuario de prueba
        if (!usuarioRepository.existsByEmail("put@inv.com")) {
            usuarioRepository.save(Usuario.builder()
                    .nombre("Put Tester").email("put@inv.com")
                    .password(passwordEncoder.encode("Test123!"))
                    .rol(Usuario.Rol.ADMIN).build());
        }
        token = jwtUtils.generateToken("put@inv.com");

        // Categoría auxiliar
        Categoria cat = new Categoria();
        cat.setNombre("Cat-PUT-" + System.nanoTime());
        categoriaId = categoriaRepository.save(cat).getId();

        // Proveedor auxiliar
        Proveedor prov = Proveedor.builder()
                .nombre("Prov-PUT-" + System.nanoTime())
                .build();
        proveedorId = proveedorRepository.save(prov).getId();

        // Producto base que se actualizará
        Producto prod = new Producto();
        prod.setNombre("Producto Original");
        prod.setPrecio(100.0);
        prod.setStock(10);
        prod.setActivo(true);
        productoId = productoRepository.save(prod).getId();
    }

    // -------------------------------------------------------------------------
    // Casos felices
    // -------------------------------------------------------------------------

    @Test
    @Order(1)
    @DisplayName("PUT /api/productos/{id} actualiza nombre y precio correctamente")
    void actualizar_nombreYPrecio_retorna200ConDatosActualizados() throws Exception {
        ProductoRequest req = new ProductoRequest();
        req.setNombre("Producto Actualizado");
        req.setPrecio(250.0);
        req.setStock(20);

        mockMvc.perform(put("/api/productos/" + productoId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Producto Actualizado"))
                .andExpect(jsonPath("$.precio").value(250.0))
                .andExpect(jsonPath("$.stock").value(20));
    }

    @Test
    @Order(2)
    @DisplayName("PUT /api/productos/{id} asigna categoría existente correctamente")
    void actualizar_conCategoriaValida_retorna200ConCategoria() throws Exception {
        ProductoRequest req = new ProductoRequest();
        req.setNombre("Producto Con Categoria");
        req.setPrecio(300.0);
        req.setStock(5);
        req.setCategoriaId(categoriaId);

        mockMvc.perform(put("/api/productos/" + productoId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Producto Con Categoria"))
                .andExpect(jsonPath("$.categoria.id").value(categoriaId));
    }

    @Test
    @Order(3)
    @DisplayName("PUT /api/productos/{id} asigna proveedor existente correctamente")
    void actualizar_conProveedorValido_retorna200ConProveedor() throws Exception {
        ProductoRequest req = new ProductoRequest();
        req.setNombre("Producto Con Proveedor");
        req.setPrecio(150.0);
        req.setStock(8);
        req.setProveedorId(proveedorId);

        mockMvc.perform(put("/api/productos/" + productoId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.proveedor.id").value(proveedorId));
    }

    @Test
    @Order(4)
    @DisplayName("PUT /api/productos/{id} actualiza stock a cero (valor válido)")
    void actualizar_stockCero_esValido() throws Exception {
        ProductoRequest req = new ProductoRequest();
        req.setNombre("Producto Sin Stock");
        req.setPrecio(50.0);
        req.setStock(0);

        mockMvc.perform(put("/api/productos/" + productoId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(0));
    }

    // -------------------------------------------------------------------------
    // Casos de error – validaciones
    // -------------------------------------------------------------------------

    @Test
    @Order(5)
    @DisplayName("PUT /api/productos/{id} con precio negativo retorna 400")
    void actualizar_precioNegativo_retorna400() throws Exception {
        ProductoRequest req = new ProductoRequest();
        req.setNombre("Producto Inválido");
        req.setPrecio(-5.0);
        req.setStock(10);

        mockMvc.perform(put("/api/productos/" + productoId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    @DisplayName("PUT /api/productos/{id} con nombre en blanco retorna 400")
    void actualizar_nombreEnBlanco_retorna400() throws Exception {
        ProductoRequest req = new ProductoRequest();
        req.setNombre("   ");
        req.setPrecio(100.0);
        req.setStock(5);

        mockMvc.perform(put("/api/productos/" + productoId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    @DisplayName("PUT /api/productos/{id} con stock negativo retorna 400")
    void actualizar_stockNegativo_retorna400() throws Exception {
        ProductoRequest req = new ProductoRequest();
        req.setNombre("Producto Inválido");
        req.setPrecio(100.0);
        req.setStock(-1);

        mockMvc.perform(put("/api/productos/" + productoId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // -------------------------------------------------------------------------
    // Integridad referencial
    // -------------------------------------------------------------------------

    @Test
    @Order(8)
    @DisplayName("PUT /api/productos/{id} con categoriaId inexistente retorna 404")
    void actualizar_categoriaInexistente_retorna404() throws Exception {
        ProductoRequest req = new ProductoRequest();
        req.setNombre("Producto");
        req.setPrecio(100.0);
        req.setStock(5);
        req.setCategoriaId(99999L);

        mockMvc.perform(put("/api/productos/" + productoId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    @DisplayName("PUT /api/productos/{id} con proveedorId inexistente retorna 404")
    void actualizar_proveedorInexistente_retorna404() throws Exception {
        ProductoRequest req = new ProductoRequest();
        req.setNombre("Producto");
        req.setPrecio(100.0);
        req.setStock(5);
        req.setProveedorId(99999L);

        mockMvc.perform(put("/api/productos/" + productoId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(10)
    @DisplayName("PUT /api/productos/{id} con id inexistente retorna 404")
    void actualizar_productoInexistente_retorna404() throws Exception {
        ProductoRequest req = new ProductoRequest();
        req.setNombre("Producto");
        req.setPrecio(100.0);
        req.setStock(5);

        mockMvc.perform(put("/api/productos/99999")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    // -------------------------------------------------------------------------
    // Seguridad
    // -------------------------------------------------------------------------

    @Test
    @Order(11)
    @DisplayName("PUT /api/productos/{id} sin token retorna 401")
    void actualizar_sinToken_retorna401() throws Exception {
        ProductoRequest req = new ProductoRequest();
        req.setNombre("Producto");
        req.setPrecio(100.0);
        req.setStock(5);

        mockMvc.perform(put("/api/productos/" + productoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }
}
