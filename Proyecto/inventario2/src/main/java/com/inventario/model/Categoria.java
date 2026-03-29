package com.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, unique = true)
    private String nombre;

    @Size(max = 255)
    private String descripcion;

    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    private List<Producto> productos;

    public Categoria() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public List<Producto> getProductos() { return productos; }
    public void setProductos(List<Producto> productos) { this.productos = productos; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id; private String nombre; private String descripcion;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder nombre(String n) { this.nombre = n; return this; }
        public Builder descripcion(String d) { this.descripcion = d; return this; }
        public Categoria build() {
            Categoria c = new Categoria();
            c.id = this.id; c.nombre = this.nombre; c.descripcion = this.descripcion;
            return c;
        }
    }
}
