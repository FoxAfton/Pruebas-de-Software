package com.inventario.service;

import com.inventario.exception.*;
import com.inventario.model.Categoria;
import com.inventario.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    public List<Categoria> listar() { return categoriaRepository.findAll(); }

    public Categoria obtener(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
    }

    public Categoria crear(Categoria categoria) {
        if (categoriaRepository.existsByNombre(categoria.getNombre()))
            throw new BusinessException("Nombre de categoría ya registrado");
        return categoriaRepository.save(categoria);
    }

    public Categoria actualizar(Long id, Categoria datos) {
        Categoria cat = obtener(id);
        if (!cat.getNombre().equals(datos.getNombre()) && categoriaRepository.existsByNombre(datos.getNombre()))
            throw new BusinessException("Nombre de categoría ya registrado");
        cat.setNombre(datos.getNombre());
        cat.setDescripcion(datos.getDescripcion());
        return categoriaRepository.save(cat);
    }

    public void eliminar(Long id) {
        Categoria cat = obtener(id);
        if (!productoRepository.findByCategoriaId(id).isEmpty())
            throw new BusinessException("Categoría tiene productos asociados, no se puede eliminar");
        categoriaRepository.delete(cat);
    }
}
