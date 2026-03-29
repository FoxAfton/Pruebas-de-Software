package com.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventario.dto.InventarioDtos.*;
import com.inventario.model.*;
import com.inventario.model.MovimientoInventario.TipoMovimiento;
import com.inventario.repository.*;
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
class MovimientoControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired ProductoRepository productoRepository;
    @Autowired UsuarioRepository usuarioRepository;
    @Autowired JwtUtils jwtUtils;
    @Autowired PasswordEncoder passwordEncoder;

    private String token;
    private Long productoId;

    @BeforeEach
    void setUp() {
        if (!usuarioRepository.existsByEmail("mov@inv.com")) {
            usuarioRepository.save(Usuario.builder()
                    .nombre("Mov").email("mov@inv.com")
                    .password(passwordEncoder.encode("Test123!"))
                    .rol(Usuario.Rol.ADMIN).build());
        }
        token = jwtUtils.generateToken("mov@inv.com");

        Producto p = productoRepository.save(Producto.builder()
                .nombre("Producto-Mov-Test").precio(100.0).stock(50).activo(true).build());
        productoId = p.getId();
    }

    @Test
    @Order(1)
    @DisplayName("POST /api/movimientos ENTRADA incrementa stock")
    void registrarEntrada_incrementaStock_retorna201() throws Exception {
        MovimientoRequest req = new MovimientoRequest();
        req.setProductoId(productoId);
        req.setTipo(TipoMovimiento.ENTRADA);
        req.setCantidad(20);
        req.setMotivo("Reposición");

        mockMvc.perform(post("/api/movimientos")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("ENTRADA"))
                .andExpect(jsonPath("$.cantidad").value(20));
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/movimientos SALIDA con stock suficiente retorna 201")
    void registrarSalida_stockSuficiente_retorna201() throws Exception {
        MovimientoRequest req = new MovimientoRequest();
        req.setProductoId(productoId);
        req.setTipo(TipoMovimiento.SALIDA);
        req.setCantidad(5);
        req.setMotivo("Entrega");

        mockMvc.perform(post("/api/movimientos")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("SALIDA"));
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/movimientos SALIDA con stock insuficiente retorna 400")
    void registrarSalida_stockInsuficiente_retorna400() throws Exception {
        MovimientoRequest req = new MovimientoRequest();
        req.setProductoId(productoId);
        req.setTipo(TipoMovimiento.SALIDA);
        req.setCantidad(9999);
        req.setMotivo("Excesivo");

        mockMvc.perform(post("/api/movimientos")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/movimientos?productoId retorna historial")
    void listarMovimientos_retornaHistorial() throws Exception {
        mockMvc.perform(get("/api/movimientos?productoId=" + productoId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
