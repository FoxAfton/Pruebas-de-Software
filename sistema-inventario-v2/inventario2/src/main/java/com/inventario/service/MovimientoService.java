package com.inventario.service;

import com.inventario.dto.InventarioDtos.MovimientoRequest;
import com.inventario.exception.*;
import com.inventario.model.*;
import com.inventario.model.MovimientoInventario.TipoMovimiento;
import com.inventario.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovimientoService {

    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository;

    public MovimientoService(MovimientoInventarioRepository movimientoRepository,
                             ProductoRepository productoRepository) {
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
    }

    public List<MovimientoInventario> listarPorProducto(Long productoId) {
        return movimientoRepository.findByProductoIdOrderByFechaDesc(productoId);
    }

    @Transactional
    public MovimientoInventario registrar(MovimientoRequest req) {
        Producto producto = productoRepository.findById(req.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        if (req.getTipo() == TipoMovimiento.SALIDA) {
            if (producto.getStock() < req.getCantidad())
                throw new StockInsuficienteException(
                        "Stock insuficiente. Disponible: " + producto.getStock() + ", solicitado: " + req.getCantidad());
            producto.setStock(producto.getStock() - req.getCantidad());
        } else {
            producto.setStock(producto.getStock() + req.getCantidad());
        }
        productoRepository.save(producto);

        MovimientoInventario mov = MovimientoInventario.builder()
                .producto(producto)
                .tipo(req.getTipo())
                .cantidad(req.getCantidad())
                .motivo(req.getMotivo())
                .build();
        return movimientoRepository.save(mov);
    }
}
