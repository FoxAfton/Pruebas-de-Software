package com.inventario.repository;

import com.inventario.model.Categoria;
import com.inventario.model.Producto;
import com.inventario.model.Proveedor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PRUEBA 3 – Unitaria (Caja Blanca)
 * Componente : ProductoRepository (consultas JPA personalizadas)
 * Herramienta: JUnit 5 + @DataJpaTest + H2 en memoria
 * Perfil     : "test"  (application-test.properties → datasource H2)
 *
 * Cubre los siguientes escenarios del Plan de Pruebas (Objetivo #2):
 *  - findByActivoTrue (paginado)
 *  - findByActivoTrueAndStockLessThanEqual (stock bajo)
 *  - findByCategoriaId
 *  - findByProveedorId
 *
 * Al usar @DataJpaTest cada test corre dentro de una transacción que
 * se revierte al finalizar → BD limpia entre pruebas.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Prueba Unitaria – ProductoRepository (consultas JPA)")
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    // ================================================================
    // 1. findByActivoTrue (paginado)
    // ================================================================

    @Test
    @DisplayName("PR-01 | findByActivoTrue solo retorna productos con activo=true")
    void findByActivoTrue_soloRetornaActivos() {
        // Arrange: un producto activo y uno inactivo
        productoRepository.save(productoBase("Activo-A", 10, true));
        productoRepository.save(productoBase("Inactivo-B", 5, false));

        // Act
        Page<Producto> resultado = productoRepository.findByActivoTrue(PageRequest.of(0, 10));

        // Assert: solo debe aparecer el activo
        assertThat(resultado.getContent())
                .extracting(Producto::getNombre)
                .contains("Activo-A")
                .doesNotContain("Inactivo-B");
    }

    @Test
    @DisplayName("PR-02 | findByActivoTrue retorna página vacía si no hay activos")
    void findByActivoTrue_sinActivos_retornaPaginaVacia() {
        productoRepository.save(productoBase("Solo-Inactivo", 3, false));

        Page<Producto> resultado = productoRepository.findByActivoTrue(PageRequest.of(0, 10));

        assertThat(resultado.getContent()).isEmpty();
    }

    // ================================================================
    // 2. findByActivoTrueAndStockLessThanEqual (stock bajo)
    // ================================================================

    @Test
    @DisplayName("PR-03 | findByActivoTrueAndStockLessThanEqual retorna productos con stock <= umbral")
    void findByStockBajo_retornaProductosBajoUmbral() {
        productoRepository.save(productoBase("Stock-3", 3, true));
        productoRepository.save(productoBase("Stock-10", 10, true));
        productoRepository.save(productoBase("Stock-20", 20, true));

        List<Producto> resultado = productoRepository.findByActivoTrueAndStockLessThanEqual(5);

        assertThat(resultado).extracting(Producto::getNombre).contains("Stock-3");
        assertThat(resultado).extracting(Producto::getNombre).doesNotContain("Stock-10", "Stock-20");
    }

    @Test
    @DisplayName("PR-04 | findByActivoTrueAndStockLessThanEqual ignora productos inactivos aunque stock sea bajo")
    void findByStockBajo_ignoraInactivos() {
        // Activo con stock alto y un inactivo con stock bajo
        productoRepository.save(productoBase("Activo-Alto", 50, true));
        productoRepository.save(productoBase("Inactivo-Bajo", 1, false));

        List<Producto> resultado = productoRepository.findByActivoTrueAndStockLessThanEqual(10);

        assertThat(resultado).extracting(Producto::getNombre)
                .doesNotContain("Inactivo-Bajo");
    }

    @Test
    @DisplayName("PR-05 | findByActivoTrueAndStockLessThanEqual con stock igual al umbral lo incluye")
    void findByStockBajo_stockExactoAlUmbral_incluyeProducto() {
        productoRepository.save(productoBase("Stock-Exacto", 5, true));

        List<Producto> resultado = productoRepository.findByActivoTrueAndStockLessThanEqual(5);

        assertThat(resultado).extracting(Producto::getNombre).contains("Stock-Exacto");
    }

    // ================================================================
    // 3. findByCategoriaId
    // ================================================================

    @Test
    @DisplayName("PR-06 | findByCategoriaId retorna solo los productos de esa categoría")
    void findByCategoriaId_retornaProductosDeLaCategoria() {
        Categoria cat1 = categoriaRepository.save(categoriaConNombre("Cat-1"));
        Categoria cat2 = categoriaRepository.save(categoriaConNombre("Cat-2"));

        Producto p1 = productoBase("Prod-Cat1", 10, true);
        p1.setCategoria(cat1);
        Producto p2 = productoBase("Prod-Cat2", 10, true);
        p2.setCategoria(cat2);
        productoRepository.save(p1);
        productoRepository.save(p2);

        List<Producto> resultado = productoRepository.findByCategoriaId(cat1.getId());

        assertThat(resultado).extracting(Producto::getNombre)
                .contains("Prod-Cat1")
                .doesNotContain("Prod-Cat2");
    }

    @Test
    @DisplayName("PR-07 | findByCategoriaId retorna lista vacía para categoría sin productos")
    void findByCategoriaId_sinProductos_retornaVacio() {
        Categoria cat = categoriaRepository.save(categoriaConNombre("Cat-Vacia"));

        List<Producto> resultado = productoRepository.findByCategoriaId(cat.getId());

        assertThat(resultado).isEmpty();
    }

    // ================================================================
    // 4. findByProveedorId
    // ================================================================

    @Test
    @DisplayName("PR-08 | findByProveedorId retorna solo los productos de ese proveedor")
    void findByProveedorId_retornaProductosDelProveedor() {
        Proveedor prov1 = proveedorRepository.save(proveedorConNombre("Prov-X"));
        Proveedor prov2 = proveedorRepository.save(proveedorConNombre("Prov-Y"));

        Producto p1 = productoBase("Prod-Prov1", 10, true);
        p1.setProveedor(prov1);
        Producto p2 = productoBase("Prod-Prov2", 10, true);
        p2.setProveedor(prov2);
        productoRepository.save(p1);
        productoRepository.save(p2);

        List<Producto> resultado = productoRepository.findByProveedorId(prov1.getId());

        assertThat(resultado).extracting(Producto::getNombre)
                .contains("Prod-Prov1")
                .doesNotContain("Prod-Prov2");
    }

    @Test
    @DisplayName("PR-09 | findByProveedorId retorna lista vacía para proveedor sin productos")
    void findByProveedorId_sinProductos_retornaVacio() {
        Proveedor prov = proveedorRepository.save(proveedorConNombre("Prov-Sin-Productos"));

        List<Producto> resultado = productoRepository.findByProveedorId(prov.getId());

        assertThat(resultado).isEmpty();
    }

    // ================================================================
    // Métodos auxiliares
    // ================================================================

    private Producto productoBase(String nombre, int stock, boolean activo) {
        return Producto.builder()
                .nombre(nombre)
                .precio(100.0)
                .stock(stock)
                .activo(activo)
                .build();
    }

    private Categoria categoriaConNombre(String nombre) {
        return Categoria.builder().nombre(nombre).build();
    }

    private Proveedor proveedorConNombre(String nombre) {
        Proveedor p = new Proveedor();
        p.setNombre(nombre);
        return p;
    }
}
