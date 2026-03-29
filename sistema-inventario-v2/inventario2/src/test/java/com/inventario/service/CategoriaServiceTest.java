package com.inventario.service;

import com.inventario.exception.*;
import com.inventario.model.Categoria;
import com.inventario.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock CategoriaRepository categoriaRepository;
    @Mock ProductoRepository productoRepository;
    @InjectMocks CategoriaService categoriaService;

    @Test
    @DisplayName("Crear categoría con nombre válido debe persistir y retornar")
    void crearCategoria_nombreValido_retornaCategoria() {
        Categoria cat = Categoria.builder().nombre("Electrónica").descripcion("Dispositivos").build();
        when(categoriaRepository.existsByNombre("Electrónica")).thenReturn(false);
        when(categoriaRepository.save(cat)).thenReturn(Categoria.builder().id(1L).nombre("Electrónica").build());

        Categoria result = categoriaService.crear(cat);

        assertThat(result.getId()).isEqualTo(1L);
        verify(categoriaRepository).save(cat);
    }

    @Test
    @DisplayName("Crear categoría con nombre duplicado debe lanzar BusinessException")
    void crearCategoria_nombreDuplicado_lanzaBusinessException() {
        Categoria cat = Categoria.builder().nombre("Electrónica").build();
        when(categoriaRepository.existsByNombre("Electrónica")).thenReturn(true);

        assertThatThrownBy(() -> categoriaService.crear(cat))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ya registrado");
    }

    @Test
    @DisplayName("Obtener categoría inexistente lanza ResourceNotFoundException")
    void obtenerCategoria_idInexistente_lanzaResourceNotFoundException() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoriaService.obtener(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Eliminar categoría con productos lanza BusinessException")
    void eliminarCategoria_conProductos_lanzaBusinessException() {
        Categoria cat = Categoria.builder().id(1L).nombre("Test").build();
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(productoRepository.findByCategoriaId(1L)).thenReturn(List.of(new com.inventario.model.Producto()));

        assertThatThrownBy(() -> categoriaService.eliminar(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("productos asociados");
    }

    @Test
    @DisplayName("Eliminar categoría sin productos debe ejecutarse sin error")
    void eliminarCategoria_sinProductos_exitoso() {
        Categoria cat = Categoria.builder().id(1L).nombre("Test").build();
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(productoRepository.findByCategoriaId(1L)).thenReturn(Collections.emptyList());

        assertThatCode(() -> categoriaService.eliminar(1L)).doesNotThrowAnyException();
        verify(categoriaRepository).delete(cat);
    }
}
