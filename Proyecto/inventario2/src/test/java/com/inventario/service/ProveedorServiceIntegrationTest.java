package com.inventario.service;

import com.inventario.exception.BusinessException;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.model.Producto;
import com.inventario.model.Proveedor;
import com.inventario.repository.ProductoRepository;
import com.inventario.repository.ProveedorRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * PRUEBA 4 - Integracion (Caja Negra)
 * Componente : ProveedorService (flujo CRUD completo)
 * Herramienta: JUnit 5 + Spring Boot Test + H2 en memoria
 * Perfil     : "test"  (application-test.properties -> datasource H2)
 *
 * Escenarios cubiertos del Plan de Pruebas (Objetivo #3):
 *  - Crear proveedor con datos validos y completos
 *  - Crear proveedor con nombre nulo (falla de constraint en BD)
 *  - Listar todos los proveedores
 *  - Obtener por ID existente e inexistente
 *  - Buscar por nombre parcial, case-insensitive
 *  - Actualizar campos
 *  - Eliminar proveedor sin productos
 *  - Impedir eliminacion con productos asociados  <- regla de negocio
 *  - Eliminar ID inexistente
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Prueba de Integracion - ProveedorService (CRUD completo)")
class ProveedorServiceIntegrationTest {

    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // ================================================================
    // 1. CREAR PROVEEDOR
    // ================================================================

    @Test
    @DisplayName("PV-01 | Crear proveedor con nombre valido lo persiste en BD")
    void crear_nombreValido_debeGuardarseEnBD() {
        Proveedor nuevo = proveedorConNombre("Distribuidora Norte");

        Proveedor guardado = proveedorService.crear(nuevo);

        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getNombre()).isEqualTo("Distribuidora Norte");
        assertThat(proveedorRepository.existsById(guardado.getId())).isTrue();
    }

    @Test
    @DisplayName("PV-02 | Crear proveedor con todos los campos opcionales los persiste completos")
    void crear_proveedorCompleto_debeGuardarTodosLosCampos() {
        Proveedor nuevo = new Proveedor();
        nuevo.setNombre("Importaciones XYZ");
        nuevo.setRfc("IXY980101AAA");
        nuevo.setTelefono("6441234567");
        nuevo.setEmail("contacto@xyz.com");

        Proveedor guardado = proveedorService.crear(nuevo);

        assertThat(guardado.getRfc()).isEqualTo("IXY980101AAA");
        assertThat(guardado.getTelefono()).isEqualTo("6441234567");
        assertThat(guardado.getEmail()).isEqualTo("contacto@xyz.com");
    }

    @Test
    @DisplayName("PV-03 | Crear proveedor con nombre nulo lanza excepcion de constraint en BD")
    void crear_nombreNulo_debeLanzarExcepcionDeConstraint() {
        Proveedor invalido = new Proveedor();
        invalido.setNombre(null);

        // El service no valida en capa negocio; la constraint NOT NULL la lanza JPA al flush
        assertThatThrownBy(() -> {
            proveedorService.crear(invalido);
            proveedorRepository.flush();
        }).isInstanceOf(Exception.class);
    }

    // ================================================================
    // 2. LEER PROVEEDOR
    // ================================================================

    @Test
    @DisplayName("PV-04 | Obtener proveedor por ID existente retorna el proveedor correcto")
    void obtener_idExistente_debeRetornarProveedor() {
        Proveedor creado = proveedorService.crear(proveedorConNombre("Electro Sur"));

        Proveedor encontrado = proveedorService.obtener(creado.getId());

        assertThat(encontrado.getId()).isEqualTo(creado.getId());
        assertThat(encontrado.getNombre()).isEqualTo("Electro Sur");
    }

    @Test
    @DisplayName("PV-05 | Obtener proveedor con ID inexistente lanza ResourceNotFoundException")
    void obtener_idInexistente_debeLanzarResourceNotFoundException() {
        assertThatThrownBy(() -> proveedorService.obtener(77777L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("77777");
    }

    @Test
    @DisplayName("PV-06 | Listar retorna todos los proveedores creados")
    void listar_variosProveedoresCreados_debeRetornarTodos() {
        proveedorService.crear(proveedorConNombre("Prov-Alpha"));
        proveedorService.crear(proveedorConNombre("Prov-Beta"));
        proveedorService.crear(proveedorConNombre("Prov-Gamma"));

        List<Proveedor> lista = proveedorService.listar();

        assertThat(lista).extracting(Proveedor::getNombre)
                .contains("Prov-Alpha", "Prov-Beta", "Prov-Gamma");
    }

    // ================================================================
    // 3. BUSCAR POR NOMBRE
    // ================================================================

    @Test
    @DisplayName("PV-07 | Buscar por nombre parcial retorna coincidencias sin importar mayusculas")
    void buscarPorNombre_parcialCaseInsensitive_retornaCoincidencias() {
        proveedorService.crear(proveedorConNombre("Grupo Industrial Sonora"));
        proveedorService.crear(proveedorConNombre("Grupo Comercial Obregon"));
        proveedorService.crear(proveedorConNombre("Importadora del Pacifico"));

        List<Proveedor> resultado = proveedorRepository.findByNombreContainingIgnoreCase("grupo");

        assertThat(resultado).hasSizeGreaterThanOrEqualTo(2);
        assertThat(resultado).extracting(Proveedor::getNombre)
                .contains("Grupo Industrial Sonora", "Grupo Comercial Obregon");
    }

    @Test
    @DisplayName("PV-08 | Buscar por nombre sin coincidencias retorna lista vacia")
    void buscarPorNombre_sinCoincidencias_retornaListaVacia() {
        proveedorService.crear(proveedorConNombre("Unico Proveedor SA"));

        List<Proveedor> resultado = proveedorRepository.findByNombreContainingIgnoreCase("XYZ_NO_EXISTE");

        assertThat(resultado).isEmpty();
    }

    // ================================================================
    // 4. ACTUALIZAR PROVEEDOR
    // ================================================================

    @Test
    @DisplayName("PV-09 | Actualizar nombre y campos opcionales modifica el proveedor en BD")
    void actualizar_camposValidos_debeModificarProveedorEnBD() {
        Proveedor original = proveedorService.crear(proveedorConNombre("Nombre Antiguo"));

        Proveedor datosNuevos = new Proveedor();
        datosNuevos.setNombre("Nombre Actualizado");
        datosNuevos.setRfc("NAC010101BBB");
        datosNuevos.setTelefono("6449876543");
        datosNuevos.setEmail("nuevo@proveedor.com");

        Proveedor actualizado = proveedorService.actualizar(original.getId(), datosNuevos);

        assertThat(actualizado.getNombre()).isEqualTo("Nombre Actualizado");
        assertThat(actualizado.getRfc()).isEqualTo("NAC010101BBB");
        assertThat(actualizado.getEmail()).isEqualTo("nuevo@proveedor.com");
    }

    @Test
    @DisplayName("PV-10 | Actualizar proveedor con ID inexistente lanza ResourceNotFoundException")
    void actualizar_idInexistente_debeLanzarResourceNotFoundException() {
        Proveedor datos = proveedorConNombre("Cualquiera");

        assertThatThrownBy(() -> proveedorService.actualizar(88888L, datos))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ================================================================
    // 5. ELIMINAR PROVEEDOR
    // ================================================================

    @Test
    @DisplayName("PV-11 | Eliminar proveedor sin productos lo borra de la BD")
    void eliminar_proveedorSinProductos_debeEliminarseCorrectamente() {
        Proveedor prov = proveedorService.crear(proveedorConNombre("Proveedor Temporal"));
        Long id = prov.getId();

        proveedorService.eliminar(id);

        assertThat(proveedorRepository.existsById(id)).isFalse();
    }

    @Test
    @DisplayName("PV-12 | Eliminar proveedor con productos asociados lanza BusinessException")
    void eliminar_conProductosAsociados_debeLanzarBusinessException() {
        Proveedor prov = proveedorService.crear(proveedorConNombre("Prov-Con-Productos"));

        Producto producto = Producto.builder()
                .nombre("Producto del proveedor")
                .precio(200.0)
                .stock(5)
                .activo(true)
                .proveedor(prov)
                .build();
        productoRepository.save(producto);

        assertThatThrownBy(() -> proveedorService.eliminar(prov.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("productos activos");
    }

    @Test
    @DisplayName("PV-13 | Eliminar proveedor con ID inexistente lanza ResourceNotFoundException")
    void eliminar_idInexistente_debeLanzarResourceNotFoundException() {
        assertThatThrownBy(() -> proveedorService.eliminar(66666L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ================================================================
    // Metodo auxiliar
    // ================================================================
    private Proveedor proveedorConNombre(String nombre) {
        Proveedor p = new Proveedor();
        p.setNombre(nombre);
        return p;
    }
}
