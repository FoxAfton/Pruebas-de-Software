package com.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_inventario")
public class MovimientoInventario {

    public enum TipoMovimiento { ENTRADA, SALIDA }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimiento tipo;

    @NotNull
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer cantidad;

    @Size(max = 255)
    private String motivo;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    @NotNull(message = "El producto es obligatorio")
    private Producto producto;

    public MovimientoInventario() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TipoMovimiento getTipo() { return tipo; }
    public void setTipo(TipoMovimiento tipo) { this.tipo = tipo; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private TipoMovimiento tipo; private Integer cantidad;
        private String motivo; private Producto producto;
        private LocalDateTime fecha = LocalDateTime.now();
        public Builder tipo(TipoMovimiento t) { this.tipo = t; return this; }
        public Builder cantidad(Integer c) { this.cantidad = c; return this; }
        public Builder motivo(String m) { this.motivo = m; return this; }
        public Builder producto(Producto p) { this.producto = p; return this; }
        public Builder fecha(LocalDateTime f) { this.fecha = f; return this; }
        public MovimientoInventario build() {
            MovimientoInventario m = new MovimientoInventario();
            m.tipo = this.tipo; m.cantidad = this.cantidad;
            m.motivo = this.motivo; m.producto = this.producto; m.fecha = this.fecha;
            return m;
        }
    }
}
