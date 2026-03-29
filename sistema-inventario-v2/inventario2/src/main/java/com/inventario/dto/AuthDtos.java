package com.inventario.dto;

import jakarta.validation.constraints.*;

public class AuthDtos {

    public static class LoginRequest {
        @NotBlank @Email
        private String email;
        @NotBlank
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginResponse {
        private String token;
        private String email;
        private String nombre;
        private String rol;

        public LoginResponse(String token, String email, String nombre, String rol) {
            this.token = token; this.email = email;
            this.nombre = nombre; this.rol = rol;
        }
        public String getToken() { return token; }
        public String getEmail() { return email; }
        public String getNombre() { return nombre; }
        public String getRol() { return rol; }
    }

    public static class RegisterRequest {
        @NotBlank private String nombre;
        @NotBlank @Email private String email;
        @NotBlank @Size(min = 6) private String password;
        private String rol;

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRol() { return rol; }
        public void setRol(String rol) { this.rol = rol; }
    }
}
