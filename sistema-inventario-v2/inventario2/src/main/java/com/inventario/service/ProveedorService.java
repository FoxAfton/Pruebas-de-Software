package com.inventario.service;

import com.inventario.exception.*;
import com.inventario.model.Proveedor;
import com.inventario.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;

    public ProveedorService(ProveedorRepository proveedorRepository, ProductoRepository productoRepository) {
        this.proveedorRepository = proveedorRepository;
        this.productoRepository = productoRepository;
    }

    public List<Proveedor> listar() { return proveedorRepository.findAll(); }

    public List<Proveedor> buscarPorNombre(String nombre) {
        return proveedorRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public Proveedor obtener(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con id: " + id));
    }

    public Proveedor crear(Proveedor proveedor) { return proveedorRepository.save(proveedor); }

    public Proveedor actualizar(Long id, Proveedor datos) {
        Proveedor p = obtener(id);
        p.setNombre(datos.getNombre());
        p.setRfc(datos.getRfc());
        p.setTelefono(datos.getTelefono());
        p.setEmail(datos.getEmail());
        return proveedorRepository.save(p);
    }

    public void eliminar(Long id) {
        Proveedor p = obtener(id);
        if (!productoRepository.findByProveedorId(id).isEmpty())
            throw new BusinessException("El proveedor tiene productos activos asociados");
        proveedorRepository.delete(p);
    }
}
