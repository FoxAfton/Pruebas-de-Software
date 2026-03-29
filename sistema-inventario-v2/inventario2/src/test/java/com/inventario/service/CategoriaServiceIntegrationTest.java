package com.inventario.service;

import com.inventario.exception.BusinessException;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.model.Categoria;
import com.inventario.model.Producto;
import com.inventario.repository.CategoriaRepository;
import com.inventario.repository.ProductoRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * PRUEBA 2 – Integración (Caja Negra)
 * Componente : CategoriaService (flujo CRUD completo)
 * Herramienta: JUnit 5 + Spring Boot Test + H2 en memoria
 * Perfil     : "test"  (application-test.properties → datasource H2)
 *
 * Cubre los siguientes escenarios del Plan de Pruebas:
 *  - Crear categoría correctamente
 *  - Leer categoría por ID
 *  - Listar todas las categorías
 *  - Actualizar nombre y descripción
 *  - Validar nombre duplicado al crear y al actualizar
 *  - Eliminar categoría sin productos asociados
 *  - Impedir eliminación de categoría con productos activos
 *  - Lanzar excepción al buscar un ID inexistente
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional          // Cada test revierte sus cambios → BD limpia entre pruebas
@DisplayName("Prueba de Integración – CategoriaService (CRUD completo)")
class CategoriaServiceIntegrationTest {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // ================================================================
    // 1. CREAR CATEGORÍA
    // ================================================================

    @Test
    @DisplayName("CI-01 | Crear categoría con datos válidos la persiste correctamente")
    void crear_categoriaValida_debeGuardarseEnBD() {
        Categoria nueva = categoriaConNombre("Electrónica");

        Categoria guardada = categoriaService.crear(nueva);

        assertThat(guardada.getId()).isNotNull();
        assertThat(guardada.getNombre()).isEqualTo("Electrónica");
        // Verificar que realmente está en BD
        assertThat(categoriaRepository.existsById(guardada.getId())).isTrue();
    }

    @Test
    @DisplayName("CI-02 | Crear categoría con descripción opcional la persiste completa")
    void crear_categoriaConDescripcion_debeGuardarDescripcion() {
        Categoria nueva = new Categoria();
        nueva.setNombre("Herramientas");
        nueva.setDescripcion("Herramientas manuales y eléctricas");

        Categoria guardada = categoriaService.crear(nueva);

        assertThat(guardada.getDescripcion()).isEqualTo("Herramientas manuales y eléctricas");
    }

    @Test
    @DisplayName("CI-03 | Crear dos categorías con el mismo nombre lanza BusinessException")
    void crear_nombreDuplicado_debeLanzarBusinessException() {
        categoriaService.crear(categoriaConNombre("Ropa"));

        Categoria duplicada = categoriaConNombre("Ropa");

        assertThatThrownBy(() -> categoriaService.crear(duplicada))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ya registrado");
    }

    // ================================================================
    // 2. LEER CATEGORÍA
    // ================================================================

    @Test
    @DisplayName("CI-04 | Obtener categoría por ID existente retorna la categoría correcta")
    void obtener_idExistente_debeRetornarCategoria() {
        Categoria creada = categoriaService.crear(categoriaConNombre("Muebles"));

        Categoria encontrada = categoriaService.obtener(creada.getId());

        assertThat(encontrada.getId()).isEqualTo(creada.getId());
        assertThat(encontrada.getNombre()).isEqualTo("Muebles");
    }

    @Test
    @DisplayName("CI-05 | Obtener categoría con ID inexistente lanza ResourceNotFoundException")
    void obtener_idInexistente_debeLanzarResourceNotFoundException() {
        Long idInexistente = 99999L;

        assertThatThrownBy(() -> categoriaService.obtener(idInexistente))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99999");
    }

    @Test
    @DisplayName("CI-06 | Listar retorna todas las categorías creadas")
    void listar_variasCategoriasCreadas_debeRetornarLista() {
        categoriaService.crear(categoriaConNombre("Alimentos"));
        categoriaService.crear(categoriaConNombre("Bebidas"));
        categoriaService.crear(categoriaConNombre("Limpieza"));

        List<Categoria> lista = categoriaService.listar();

        // Puede haber datos de seed en DataInitializer; verificamos que contenga las nuestras
        assertThat(lista).extracting(Categoria::getNombre)
                .contains("Alimentos", "Bebidas", "Limpieza");
    }

    // ================================================================
    // 3. ACTUALIZAR CATEGORÍA
    // ================================================================

    @Test
    @DisplayName("CI-07 | Actualizar nombre y descripción de una categoría existente")
    void actualizar_camposValidos_debeModificarCategoriaEnBD() {
        Categoria original = categoriaService.crear(categoriaConNombre("Papeleria"));

        Categoria datosNuevos = new Categoria();
        datosNuevos.setNombre("Papelería Fina");
        datosNuevos.setDescripcion("Artículos de escritura premium");

        Categoria actualizada = categoriaService.actualizar(original.getId(), datosNuevos);

        assertThat(actualizada.getNombre()).isEqualTo("Papelería Fina");
        assertThat(actualizada.getDescripcion()).isEqualTo("Artículos de escritura premium");
    }

    @Test
    @DisplayName("CI-08 | Actualizar con mismo nombre (sin cambio) no lanza excepción")
    void actualizar_mismoNombre_noDebeGenerarErrorDeDuplicado() {
        Categoria original = categoriaService.crear(categoriaConNombre("Jardinería"));

        Categoria mismoNombre = new Categoria();
        mismoNombre.setNombre("Jardinería"); // mismo nombre, sin cambio
        mismoNombre.setDescripcion("Plantas y accesorios");

        assertThatCode(() -> categoriaService.actualizar(original.getId(), mismoNombre))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("CI-09 | Actualizar con nombre de otra categoría existente lanza BusinessException")
    void actualizar_nombreYaUsadoPorOtra_debeLanzarBusinessException() {
        categoriaService.crear(categoriaConNombre("CategoriaA"));
        Categoria catB = categoriaService.crear(categoriaConNombre("CategoriaB"));

        Categoria datos = new Categoria();
        datos.setNombre("CategoriaA"); // nombre de la primera categoría

        assertThatThrownBy(() -> categoriaService.actualizar(catB.getId(), datos))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ya registrado");
    }

    // ================================================================
    // 4. ELIMINAR CATEGORÍA
    // ================================================================

    @Test
    @DisplayName("CI-10 | Eliminar categoría sin productos asociados la borra de la BD")
    void eliminar_sinProductos_debeEliminarseCorrectamente() {
        Categoria cat = categoriaService.crear(categoriaConNombre("Temporal"));
        Long id = cat.getId();

        categoriaService.eliminar(id);

        assertThat(categoriaRepository.existsById(id)).isFalse();
    }

    @Test
    @DisplayName("CI-11 | Eliminar categoría con productos asociados lanza BusinessException")
    void eliminar_conProductosAsociados_debeLanzarBusinessException() {
        // Arrange: crear categoría y un producto vinculado
        Categoria cat = categoriaService.crear(categoriaConNombre("Deportes"));

        Producto producto = Producto.builder()
                .nombre("Pelota de fútbol")
                .precio(250.00)
                .stock(10)
                .activo(true)
                .categoria(cat)
                .build();
        productoRepository.save(producto);

        // Act & Assert
        assertThatThrownBy(() -> categoriaService.eliminar(cat.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("productos asociados");
    }

    @Test
    @DisplayName("CI-12 | Eliminar categoría con ID inexistente lanza ResourceNotFoundException")
    void eliminar_idInexistente_debeLanzarResourceNotFoundException() {
        assertThatThrownBy(() -> categoriaService.eliminar(88888L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ================================================================
    // Método auxiliar
    // ================================================================
    private Categoria categoriaConNombre(String nombre) {
        return Categoria.builder().nombre(nombre).build();
    }
}
