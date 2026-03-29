package com.inventario.repository;

import com.inventario.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Page<Producto> findByActivoTrue(Pageable pageable);
    List<Producto> findByActivoTrueAndStockLessThanEqual(Integer umbral);
    List<Producto> findByCategoriaId(Long categoriaId);
    List<Producto> findByProveedorId(Long proveedorId);
}
