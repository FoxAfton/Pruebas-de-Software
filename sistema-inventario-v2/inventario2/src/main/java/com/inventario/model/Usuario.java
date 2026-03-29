package com.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    public enum Rol { ADMIN, OPERADOR }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String nombre;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol = Rol.OPERADOR;

    @Column(nullable = false)
    private Boolean activo = true;

    public Usuario() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String nombre; private String email;
        private String password; private Rol rol = Rol.OPERADOR; private Boolean activo = true;
        public Builder nombre(String n) { this.nombre = n; return this; }
        public Builder email(String e) { this.email = e; return this; }
        public Builder password(String p) { this.password = p; return this; }
        public Builder rol(Rol r) { this.rol = r; return this; }
        public Builder activo(Boolean a) { this.activo = a; return this; }
        public Usuario build() {
            Usuario u = new Usuario();
            u.nombre = this.nombre; u.email = this.email;
            u.password = this.password; u.rol = this.rol; u.activo = this.activo;
            return u;
        }
    }
}
