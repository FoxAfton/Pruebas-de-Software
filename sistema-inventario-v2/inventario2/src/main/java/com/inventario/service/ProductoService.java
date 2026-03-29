package com.inventario.service;

import com.inventario.dto.InventarioDtos.ProductoRequest;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.model.*;
import com.inventario.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProveedorRepository proveedorRepository;

    public ProductoService(ProductoRepository productoRepository,
                           CategoriaRepository categoriaRepository,
                           ProveedorRepository proveedorRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.proveedorRepository = proveedorRepository;
    }

    public Page<Producto> listar(int page, int size) {
        return productoRepository.findByActivoTrue(PageRequest.of(page, size));
    }

    public Producto obtener(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
    }

    public List<Producto> stockBajo(int umbral) {
        return productoRepository.findByActivoTrueAndStockLessThanEqual(umbral);
    }

    public Producto crear(ProductoRequest req) {
        Producto p = new Producto();
        p.setNombre(req.getNombre());
        p.setDescripcion(req.getDescripcion());
        p.setPrecio(req.getPrecio());
        p.setStock(req.getStock());
        p.setActivo(true);
        if (req.getCategoriaId() != null)
            p.setCategoria(categoriaRepository.findById(req.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada")));
        if (req.getProveedorId() != null)
            p.setProveedor(proveedorRepository.findById(req.getProveedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado")));
        return productoRepository.save(p);
    }

    public Producto actualizar(Long id, ProductoRequest req) {
        Producto p = obtener(id);
        p.setNombre(req.getNombre());
        p.setDescripcion(req.getDescripcion());
        p.setPrecio(req.getPrecio());
        p.setStock(req.getStock());
        if (req.getCategoriaId() != null)
            p.setCategoria(categoriaRepository.findById(req.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada")));
        if (req.getProveedorId() != null)
            p.setProveedor(proveedorRepository.findById(req.getProveedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado")));
        return productoRepository.save(p);
    }

    public void eliminar(Long id) {
        Producto p = obtener(id);
        p.setActivo(false);
        productoRepository.save(p);
    }
}
