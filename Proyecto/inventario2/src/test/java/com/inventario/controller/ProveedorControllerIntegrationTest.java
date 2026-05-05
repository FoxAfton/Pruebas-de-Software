package com.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventario.model.Categoria;
import com.inventario.model.Producto;
import com.inventario.model.Proveedor;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para /api/proveedores (CRUD completo)
 * Tipo: Caja negra | Nivel: Sistema
 * Herramienta: JUnit 5 + Spring Boot Test
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProveedorControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UsuarioRepository usuarioRepository;
    @Autowired ProveedorRepository proveedorRepository;
    @Autowired ProductoRepository productoRepository;
    @Autowired CategoriaRepository categoriaRepository;
    @Autowired JwtUtils jwtUtils;
    @Autowired PasswordEncoder passwordEncoder;

    private String token;

    @BeforeEach
    void setUp() {
        if (!usuarioRepository.existsByEmail("prov@inv.com")) {
            usuarioRepository.save(Usuario.builder()
                    .nombre("Prov Tester").email("prov@inv.com")
                    .password(passwordEncoder.encode("Test123!"))
                    .rol(Usuario.Rol.ADMIN).build());
        }
        token = jwtUtils.generateToken("prov@inv.com");
    }

    // -------------------------------------------------------------------------
    // POST /api/proveedores
    // -------------------------------------------------------------------------

    @Test
    @Order(1)
    @DisplayName("POST /api/proveedores con datos válidos retorna 201")
    void crear_datosValidos_retorna201() throws Exception {
        Proveedor prov = Proveedor.builder()
                .nombre("Distribuidora Norteña")
                .rfc("DNO850101XX0")
                .telefono("6441234567")
                .email("norteña@ejemplo.com")
                .build();

        mockMvc.perform(post("/api/proveedores")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prov)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("Distribuidora Norteña"))
                .andExpect(jsonPath("$.rfc").value("DNO850101XX0"));
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/proveedores con nombre en blanco retorna 400")
    void crear_nombreEnBlanco_retorna400() throws Exception {
        Proveedor prov = Proveedor.builder().nombre("   ").build();

        mockMvc.perform(post("/api/proveedores")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prov)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/proveedores sin token retorna 401")
    void crear_sinToken_retorna401() throws Exception {
        Proveedor prov = Proveedor.builder().nombre("Proveedor Sin Auth").build();

        mockMvc.perform(post("/api/proveedores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prov)))
                .andExpect(status().isUnauthorized());
    }

    // -------------------------------------------------------------------------
    // GET /api/proveedores
    // -------------------------------------------------------------------------

    @Test
    @Order(4)
    @DisplayName("GET /api/proveedores retorna lista con status 200")
    void listar_retorna200ConLista() throws Exception {
        // Aseguramos al menos un proveedor en BD
        proveedorRepository.save(Proveedor.builder()
                .nombre("Prov-List-" + System.nanoTime()).build());

        mockMvc.perform(get("/api/proveedores")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    @Order(5)
    @DisplayName("GET /api/proveedores?nombre= filtra por nombre (búsqueda parcial)")
    void listar_filtroPorNombre_retornaCoincidencias() throws Exception {
        String unico = "ProvBuscable-" + System.nanoTime();
        proveedorRepository.save(Proveedor.builder().nombre(unico).build());

        mockMvc.perform(get("/api/proveedores?nombre=" + unico.substring(0, 10))
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nombre", containsStringIgnoringCase(unico.substring(0, 10))));
    }

    // -------------------------------------------------------------------------
    // GET /api/proveedores/{id}
    // -------------------------------------------------------------------------

    @Test
    @Order(6)
    @DisplayName("GET /api/proveedores/{id} existente retorna el proveedor")
    void obtener_idExistente_retorna200() throws Exception {
        Proveedor guardado = proveedorRepository.save(
                Proveedor.builder().nombre("Prov-Get-" + System.nanoTime()).build());

        mockMvc.perform(get("/api/proveedores/" + guardado.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(guardado.getId()))
                .andExpect(jsonPath("$.nombre").value(guardado.getNombre()));
    }

    @Test
    @Order(7)
    @DisplayName("GET /api/proveedores/{id} inexistente retorna 404")
    void obtener_idInexistente_retorna404() throws Exception {
        mockMvc.perform(get("/api/proveedores/99999")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    // -------------------------------------------------------------------------
    // PUT /api/proveedores/{id}
    // -------------------------------------------------------------------------

    @Test
    @Order(8)
    @DisplayName("PUT /api/proveedores/{id} actualiza campos correctamente")
    void actualizar_camposValidos_retorna200ConDatosNuevos() throws Exception {
        Proveedor original = proveedorRepository.save(
                Proveedor.builder().nombre("Prov-Original-" + System.nanoTime()).build());

        Proveedor actualizado = Proveedor.builder()
                .nombre("Prov-Actualizado")
                .telefono("6449876543")
                .email("nuevo@ejemplo.com")
                .build();

        mockMvc.perform(put("/api/proveedores/" + original.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Prov-Actualizado"))
                .andExpect(jsonPath("$.telefono").value("6449876543"));
    }

    @Test
    @Order(9)
    @DisplayName("PUT /api/proveedores/{id} inexistente retorna 404")
    void actualizar_idInexistente_retorna404() throws Exception {
        Proveedor prov = Proveedor.builder().nombre("Fantasma").build();

        mockMvc.perform(put("/api/proveedores/99999")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prov)))
                .andExpect(status().isNotFound());
    }

    // -------------------------------------------------------------------------
    // DELETE /api/proveedores/{id}
    // -------------------------------------------------------------------------

    @Test
    @Order(10)
    @DisplayName("DELETE /api/proveedores/{id} sin productos activos retorna 204")
    void eliminar_sinProductosActivos_retorna204() throws Exception {
        Proveedor prov = proveedorRepository.save(
                Proveedor.builder().nombre("Prov-Delete-" + System.nanoTime()).build());

        mockMvc.perform(delete("/api/proveedores/" + prov.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(11)
    @DisplayName("DELETE /api/proveedores/{id} con productos activos retorna 409")
    void eliminar_conProductosActivos_retorna409() throws Exception {
        // Proveedor con un producto activo asociado
        Proveedor prov = proveedorRepository.save(
                Proveedor.builder().nombre("Prov-ConProd-" + System.nanoTime()).build());

        Producto prod = new Producto();
        prod.setNombre("Prod-Del-" + System.nanoTime());
        prod.setPrecio(10.0);
        prod.setStock(1);
        prod.setActivo(true);
        prod.setProveedor(prov);
        productoRepository.save(prod);

        mockMvc.perform(delete("/api/proveedores/" + prov.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(12)
    @DisplayName("DELETE /api/proveedores/{id} inexistente retorna 404")
    void eliminar_idInexistente_retorna404() throws Exception {
        mockMvc.perform(delete("/api/proveedores/99999")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
