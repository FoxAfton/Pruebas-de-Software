package com.inventario.controller;

import com.inventario.dto.AuthDtos.*;
import com.inventario.model.Usuario;
import com.inventario.repository.UsuarioRepository;
import com.inventario.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authManager, JwtUtils jwtUtils,
                          UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        Usuario usuario = usuarioRepository.findByEmail(req.getEmail()).orElseThrow();
        String token = jwtUtils.generateToken(req.getEmail());
        return ResponseEntity.ok(new LoginResponse(token, usuario.getEmail(),
                usuario.getNombre(), usuario.getRol().name()));
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@Valid @RequestBody RegisterRequest req) {
        if (usuarioRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Este correo ya está registrado"));
        }
        Usuario.Rol rol = Usuario.Rol.OPERADOR;
        try { if (req.getRol() != null) rol = Usuario.Rol.valueOf(req.getRol()); } catch (Exception ignored) {}

        Usuario nuevo = Usuario.builder()
                .nombre(req.getNombre())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .rol(rol)
                .build();
        usuarioRepository.save(nuevo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "Usuario registrado correctamente"));
    }
}
