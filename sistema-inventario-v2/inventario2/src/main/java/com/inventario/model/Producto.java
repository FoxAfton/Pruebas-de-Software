package com.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 150)
    @Column(nullable = false)
    private String nombre;

    @Size(max = 255)
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Column(nullable = false)
    private Double precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    public Producto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id; private String nombre; private String descripcion;
        private Double precio; private Integer stock; private Boolean activo = true;
        private Categoria categoria; private Proveedor proveedor;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder nombre(String n) { this.nombre = n; return this; }
        public Builder descripcion(String d) { this.descripcion = d; return this; }
        public Builder precio(Double p) { this.precio = p; return this; }
        public Builder stock(Integer s) { this.stock = s; return this; }
        public Builder activo(Boolean a) { this.activo = a; return this; }
        public Builder categoria(Categoria c) { this.categoria = c; return this; }
        public Builder proveedor(Proveedor p) { this.proveedor = p; return this; }
        public Producto build() {
            Producto p = new Producto();
            p.id = this.id; p.nombre = this.nombre; p.descripcion = this.descripcion;
            p.precio = this.precio; p.stock = this.stock; p.activo = this.activo;
            p.categoria = this.categoria; p.proveedor = this.proveedor;
            return p;
        }
    }
}
