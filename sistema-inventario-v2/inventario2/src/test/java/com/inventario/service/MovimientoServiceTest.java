package com.inventario.service;

import com.inventario.dto.InventarioDtos.MovimientoRequest;
import com.inventario.exception.*;
import com.inventario.model.*;
import com.inventario.model.MovimientoInventario.TipoMovimiento;
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
class MovimientoServiceTest {

    @Mock MovimientoInventarioRepository movimientoRepository;
    @Mock ProductoRepository productoRepository;
    @InjectMocks MovimientoService movimientoService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = Producto.builder().id(1L).nombre("Teclado").stock(10).activo(true).precio(200.0).build();
    }

    @Test
    @DisplayName("Registrar ENTRADA incrementa stock correctamente")
    void registrarEntrada_incrementaStock() {
        MovimientoRequest req = new MovimientoRequest();
        req.setProductoId(1L); req.setTipo(TipoMovimiento.ENTRADA); req.setCantidad(20); req.setMotivo("Compra");

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any())).thenReturn(producto);
        when(movimientoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MovimientoInventario result = movimientoService.registrar(req);

        assertThat(producto.getStock()).isEqualTo(30);
        assertThat(result.getTipo()).isEqualTo(TipoMovimiento.ENTRADA);
    }

    @Test
    @DisplayName("Registrar SALIDA dentro del stock disponible decrementa correctamente")
    void registrarSalida_stockSuficiente_decrementaStock() {
        MovimientoRequest req = new MovimientoRequest();
        req.setProductoId(1L); req.setTipo(TipoMovimiento.SALIDA); req.setCantidad(5); req.setMotivo("Venta");

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any())).thenReturn(producto);
        when(movimientoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        movimientoService.registrar(req);

        assertThat(producto.getStock()).isEqualTo(5);
    }

    @Test
    @DisplayName("Registrar SALIDA con cantidad mayor al stock lanza StockInsuficienteException")
    void registrarSalida_stockInsuficiente_lanzaExcepcion() {
        MovimientoRequest req = new MovimientoRequest();
        req.setProductoId(1L); req.setTipo(TipoMovimiento.SALIDA); req.setCantidad(99);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        assertThatThrownBy(() -> movimientoService.registrar(req))
                .isInstanceOf(StockInsuficienteException.class)
                .hasMessageContaining("Stock insuficiente");
    }

    @Test
    @DisplayName("Registrar movimiento con producto inexistente lanza ResourceNotFoundException")
    void registrar_productoInexistente_lanzaExcepcion() {
        MovimientoRequest req = new MovimientoRequest();
        req.setProductoId(999L); req.setTipo(TipoMovimiento.ENTRADA); req.setCantidad(5);

        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movimientoService.registrar(req))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
