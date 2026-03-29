package com.inventario.dto;

import com.inventario.model.MovimientoInventario.TipoMovimiento;
import jakarta.validation.constraints.*;

public class InventarioDtos {

    public static class ProductoRequest {
        @NotBlank private String nombre;
        private String descripcion;
        @NotNull @DecimalMin("0.01") private Double precio;
        @NotNull @Min(0) private Integer stock;
        private Long categoriaId;
        private Long proveedorId;

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        public Double getPrecio() { return precio; }
        public void setPrecio(Double precio) { this.precio = precio; }
        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }
        public Long getCategoriaId() { return categoriaId; }
        public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
        public Long getProveedorId() { return proveedorId; }
        public void setProveedorId(Long proveedorId) { this.proveedorId = proveedorId; }
    }

    public static class MovimientoRequest {
        @NotNull private Long productoId;
        @NotNull private TipoMovimiento tipo;
        @NotNull @Min(1) private Integer cantidad;
        private String motivo;

        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }
        public TipoMovimiento getTipo() { return tipo; }
        public void setTipo(TipoMovimiento tipo) { this.tipo = tipo; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
        public String getMotivo() { return motivo; }
        public void setMotivo(String motivo) { this.motivo = motivo; }
    }
}
