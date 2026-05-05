package com.inventario.controller;

import com.inventario.model.Usuario;
import com.inventario.repository.UsuarioRepository;
import com.inventario.security.JwtUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Key;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de sistema para JWT: expiración de token y acceso a rutas protegidas.
 * Tipo: Caja negra | Nivel: Sistema
 * Herramienta: JUnit 5 + Spring Boot Test
 *
 * Complementa AuthControllerIntegrationTest cubriendo los casos que requieren
 * manipulación directa del token (expirado, malformado, sin firma).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JwtAuthSystemTest {

    @Autowired MockMvc mockMvc;
    @Autowired UsuarioRepository usuarioRepository;
    @Autowired JwtUtils jwtUtils;
    @Autowired PasswordEncoder passwordEncoder;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    private static final String EMAIL = "jwt@inv.com";

    @BeforeEach
    void setUp() {
        if (!usuarioRepository.existsByEmail(EMAIL)) {
            usuarioRepository.save(Usuario.builder()
                    .nombre("JWT Tester").email(EMAIL)
                    .password(passwordEncoder.encode("Test123!"))
                    .rol(Usuario.Rol.ADMIN).build());
        }
    }

    // -------------------------------------------------------------------------
    // Acceso a rutas protegidas
    // -------------------------------------------------------------------------

    @Test
    @Order(1)
    @DisplayName("GET /api/productos sin token retorna 401")
    void rutaProtegida_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/categorias sin token retorna 401")
    void categorias_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/proveedores sin token retorna 401")
    void proveedores_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/proveedores"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/movimientos sin token retorna 401")
    void movimientos_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/movimientos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(5)
    @DisplayName("Ruta protegida con token válido retorna 200")
    void rutaProtegida_conTokenValido_retorna200() throws Exception {
        String token = jwtUtils.generateToken(EMAIL);

        mockMvc.perform(get("/api/productos")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    // -------------------------------------------------------------------------
    // Tokens malformados / inválidos
    // -------------------------------------------------------------------------

    @Test
    @Order(6)
    @DisplayName("Token con formato incorrecto (sin puntos) retorna 401")
    void tokenMalformado_sinPuntos_retorna401() throws Exception {
        mockMvc.perform(get("/api/productos")
                .header("Authorization", "Bearer esto-no-es-un-jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(7)
    @DisplayName("Token con firma incorrecta retorna 401")
    void tokenFirmaIncorrecta_retorna401() throws Exception {
        // Firmado con una clave diferente a la del servidor
        Key otraKey = Keys.hmacShaKeyFor("OtraClaveSecretaQueNoEsLaDelServidorXYZ123".getBytes());
        String tokenFalso = Jwts.builder()
                .setSubject(EMAIL)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3_600_000))
                .signWith(otraKey, SignatureAlgorithm.HS256)
                .compact();

        mockMvc.perform(get("/api/productos")
                .header("Authorization", "Bearer " + tokenFalso))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(8)
    @DisplayName("Header Authorization vacío retorna 401")
    void headerAuthorizationVacio_retorna401() throws Exception {
        mockMvc.perform(get("/api/productos")
                .header("Authorization", ""))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(9)
    @DisplayName("Header Authorization sin prefijo Bearer retorna 401")
    void tokenSinPrefijoBearerRetorna401() throws Exception {
        String token = jwtUtils.generateToken(EMAIL);

        mockMvc.perform(get("/api/productos")
                .header("Authorization", token))   // sin "Bearer "
                .andExpect(status().isUnauthorized());
    }

    // -------------------------------------------------------------------------
    // Expiración del token
    // -------------------------------------------------------------------------

    @Test
    @Order(10)
    @DisplayName("Token expirado retorna 401")
    void tokenExpirado_retorna401() throws Exception {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        // Token con fecha de expiración en el pasado
        String tokenExpirado = Jwts.builder()
                .setSubject(EMAIL)
                .setIssuedAt(new Date(System.currentTimeMillis() - 10_000))
                .setExpiration(new Date(System.currentTimeMillis() - 5_000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        mockMvc.perform(get("/api/productos")
                .header("Authorization", "Bearer " + tokenExpirado))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(11)
    @DisplayName("Token recién generado aún no ha expirado y da acceso")
    void tokenRecienGenerado_noEstaExpirado_retorna200() throws Exception {
        String token = jwtUtils.generateToken(EMAIL);

        // El token debe dar acceso inmediatamente después de generarse
        mockMvc.perform(get("/api/productos")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @Order(12)
    @DisplayName("Token sin campo de expiración (sin exp claim) retorna 401")
    void tokenSinExpiracion_retorna401() throws Exception {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        // Token sin setExpiration() — el filtro JWT debe rechazarlo
        String tokenSinExp = Jwts.builder()
                .setSubject(EMAIL)
                .setIssuedAt(new Date())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        mockMvc.perform(get("/api/productos")
                .header("Authorization", "Bearer " + tokenSinExp))
                .andExpect(status().isUnauthorized());
    }
}
