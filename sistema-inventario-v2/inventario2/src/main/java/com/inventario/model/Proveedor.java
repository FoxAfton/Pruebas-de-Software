package com.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Entity
@Table(name = "proveedores")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 150)
    @Column(nullable = false)
    private String nombre;

    @Size(max = 20)
    private String rfc;

    @Size(max = 20)
    private String telefono;

    @Size(max = 150)
    private String email;

    @OneToMany(mappedBy = "proveedor", fetch = FetchType.LAZY)
    private List<Producto> productos;

    public Proveedor() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Producto> getProductos() { return productos; }
    public void setProductos(List<Producto> productos) { this.productos = productos; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id; private String nombre; private String rfc;
        private String telefono; private String email;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder nombre(String n) { this.nombre = n; return this; }
        public Builder rfc(String r) { this.rfc = r; return this; }
        public Builder telefono(String t) { this.telefono = t; return this; }
        public Builder email(String e) { this.email = e; return this; }
        public Proveedor build() {
            Proveedor p = new Proveedor();
            p.id = this.id; p.nombre = this.nombre; p.rfc = this.rfc;
            p.telefono = this.telefono; p.email = this.email;
            return p;
        }
    }
}
