package com.inventario.controller;

import com.inventario.dto.InventarioDtos.MovimientoRequest;
import com.inventario.model.MovimientoInventario;
import com.inventario.service.MovimientoService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    private final MovimientoService movimientoService;

    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @GetMapping
    public List<MovimientoInventario> listar(@RequestParam Long productoId) {
        return movimientoService.listarPorProducto(productoId);
    }

    @PostMapping
    public ResponseEntity<MovimientoInventario> registrar(@Valid @RequestBody MovimientoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoService.registrar(req));
    }
}
