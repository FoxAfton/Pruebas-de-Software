package com.inventario.service;

import com.inventario.dto.InventarioDtos.ProductoRequest;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.model.Producto;
import com.inventario.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock ProductoRepository productoRepository;
    @Mock CategoriaRepository categoriaRepository;
    @Mock ProveedorRepository proveedorRepository;
    @InjectMocks ProductoService productoService;

    @Test
    @DisplayName("Crear producto con datos válidos lo persiste correctamente")
    void crearProducto_datosValidos_persisteCorrectamente() {
        ProductoRequest req = new ProductoRequest();
        req.setNombre("Monitor"); req.setPrecio(3500.0); req.setStock(5);

        Producto saved = Producto.builder().id(1L).nombre("Monitor").precio(3500.0).stock(5).activo(true).build();
        when(productoRepository.save(any())).thenReturn(saved);

        Producto result = productoService.crear(req);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getActivo()).isTrue();
    }

    @Test
    @DisplayName("Eliminar producto (baja lógica) pone activo=false")
    void eliminarProducto_bajLogica_activoFalse() {
        Producto p = Producto.builder().id(1L).nombre("Mouse").activo(true).stock(3).precio(150.0).build();
        when(productoRepository.findById(1L)).thenReturn(Optional.of(p));
        when(productoRepository.save(any())).thenReturn(p);

        productoService.eliminar(1L);

        assertThat(p.getActivo()).isFalse();
        verify(productoRepository).save(p);
    }

    @Test
    @DisplayName("Obtener producto inexistente lanza ResourceNotFoundException")
    void obtenerProducto_idInexistente_lanzaExcepcion() {
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.obtener(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }
}
