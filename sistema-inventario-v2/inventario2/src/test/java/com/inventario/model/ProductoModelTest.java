package com.inventario.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PRUEBA 1 – Unitaria (Caja Blanca)
 * Componente : Modelo Producto
 * Herramienta: JUnit 5 + Bean Validation (sin Spring context)
 * Plan       : Verifica tipos de datos, rangos, campos obligatorios
 *              y restricciones de negocio del modelo Producto.
 */
@DisplayName("Prueba Unitaria – Modelo Producto")
class ProductoModelTest {

    private static Validator validator;

    // ---------------------------------------------------------------
    // Configuración: inicializar el validador de Bean Validation
    // ---------------------------------------------------------------
    @BeforeAll
    static void configurarValidador() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ================================================================
    // 1. CONSTRUCTOR / BUILDER
    // ================================================================

    @Test
    @DisplayName("CP-01 | Builder crea un Producto con todos los campos correctamente")
    void builder_debeCrearProductoCompleto() {
        Categoria cat = Categoria.builder().id(1L).nombre("Electrónica").build();
        Proveedor prov = Proveedor.builder().id(2L).nombre("Proveedor A").build();

        Producto producto = Producto.builder()
                .id(10L)
                .nombre("Laptop Dell")
                .descripcion("Laptop de 15 pulgadas")
                .precio(15000.00)
                .stock(5)
                .activo(true)
                .categoria(cat)
                .proveedor(prov)
                .build();

        assertThat(producto.getId()).isEqualTo(10L);
        assertThat(producto.getNombre()).isEqualTo("Laptop Dell");
        assertThat(producto.getDescripcion()).isEqualTo("Laptop de 15 pulgadas");
        assertThat(producto.getPrecio()).isEqualTo(15000.00);
        assertThat(producto.getStock()).isEqualTo(5);
        assertThat(producto.getActivo()).isTrue();
        assertThat(producto.getCategoria().getNombre()).isEqualTo("Electrónica");
        assertThat(producto.getProveedor().getNombre()).isEqualTo("Proveedor A");
    }

    @Test
    @DisplayName("CP-02 | Constructor vacío + setters asignan valores correctamente")
    void constructorVacio_conSetters_debeAsignarCampos() {
        Producto producto = new Producto();
        producto.setNombre("Teclado");
        producto.setPrecio(350.00);
        producto.setStock(20);

        assertThat(producto.getNombre()).isEqualTo("Teclado");
        assertThat(producto.getPrecio()).isEqualTo(350.00);
        assertThat(producto.getStock()).isEqualTo(20);
    }

    @Test
    @DisplayName("CP-03 | activo es TRUE por defecto al crear un Producto")
    void activoDebeSer_trueporDefecto() {
        Producto producto = new Producto();
        assertThat(producto.getActivo()).isTrue();
    }

    // ================================================================
    // 2. VALIDACIONES DE CAMPOS OBLIGATORIOS
    // ================================================================

    @Test
    @DisplayName("CP-04 | Producto válido no genera violaciones de validación")
    void productoValido_noDebeGenerarViolaciones() {
        Producto producto = productoValido();

        Set<ConstraintViolation<Producto>> violaciones = validator.validate(producto);

        assertThat(violaciones).isEmpty();
    }

    @Test
    @DisplayName("CP-05 | nombre en blanco genera violación @NotBlank")
    void nombre_enBlanco_debeGenerarViolacion() {
        Producto producto = productoValido();
        producto.setNombre("   "); // solo espacios

        Set<ConstraintViolation<Producto>> violaciones = validator.validate(producto);

        assertThat(violaciones).isNotEmpty();
        assertThat(violaciones).anyMatch(v -> v.getPropertyPath().toString().equals("nombre"));
    }

    @Test
    @DisplayName("CP-06 | nombre nulo genera violación @NotBlank")
    void nombre_nulo_debeGenerarViolacion() {
        Producto producto = productoValido();
        producto.setNombre(null);

        Set<ConstraintViolation<Producto>> violaciones = validator.validate(producto);

        assertThat(violaciones).anyMatch(v -> v.getPropertyPath().toString().equals("nombre"));
    }

    @Test
    @DisplayName("CP-07 | precio nulo genera violación @NotNull")
    void precio_nulo_debeGenerarViolacion() {
        Producto producto = productoValido();
        producto.setPrecio(null);

        Set<ConstraintViolation<Producto>> violaciones = validator.validate(producto);

        assertThat(violaciones).anyMatch(v -> v.getPropertyPath().toString().equals("precio"));
    }

    @Test
    @DisplayName("CP-08 | stock nulo genera violación @NotNull")
    void stock_nulo_debeGenerarViolacion() {
        Producto producto = productoValido();
        producto.setStock(null);

        Set<ConstraintViolation<Producto>> violaciones = validator.validate(producto);

        assertThat(violaciones).anyMatch(v -> v.getPropertyPath().toString().equals("stock"));
    }

    // ================================================================
    // 3. VALIDACIONES DE RANGO / LÍMITES DE NEGOCIO
    // ================================================================

    @Test
    @DisplayName("CP-09 | precio de 0.01 (mínimo válido) no genera violación")
    void precio_minimoValido_noDebeGenerarViolacion() {
        Producto producto = productoValido();
        producto.setPrecio(0.01);

        Set<ConstraintViolation<Producto>> violaciones = validator.validate(producto);

        assertThat(violaciones).noneMatch(v -> v.getPropertyPath().toString().equals("precio"));
    }

    @ParameterizedTest
    @DisplayName("CP-10 | precio <= 0 genera violación @DecimalMin")
    @ValueSource(doubles = {0.0, -1.0, -100.0})
    void precio_menorOIgualACero_debeGenerarViolacion(double precioInvalido) {
        Producto producto = productoValido();
        producto.setPrecio(precioInvalido);

        Set<ConstraintViolation<Producto>> violaciones = validator.validate(producto);

        assertThat(violaciones).anyMatch(v -> v.getPropertyPath().toString().equals("precio"));
    }

    @Test
    @DisplayName("CP-11 | stock de 0 (mínimo válido) no genera violación")
    void stock_cero_esValido() {
        Producto producto = productoValido();
        producto.setStock(0);

        Set<ConstraintViolation<Producto>> violaciones = validator.validate(producto);

        assertThat(violaciones).noneMatch(v -> v.getPropertyPath().toString().equals("stock"));
    }

    @Test
    @DisplayName("CP-12 | stock negativo genera violación @Min(0)")
    void stock_negativo_debeGenerarViolacion() {
        Producto producto = productoValido();
        producto.setStock(-1);

        Set<ConstraintViolation<Producto>> violaciones = validator.validate(producto);

        assertThat(violaciones).anyMatch(v -> v.getPropertyPath().toString().equals("stock"));
    }

    @Test
    @DisplayName("CP-13 | nombre con 150 caracteres (límite máximo) es válido")
    void nombre_conLongitudMaxima_esValido() {
        Producto producto = productoValido();
        producto.setNombre("A".repeat(150)); // exactamente @Size(max=150)

        Set<ConstraintViolation<Producto>> violaciones = validator.validate(producto);

        assertThat(violaciones).noneMatch(v -> v.getPropertyPath().toString().equals("nombre"));
    }

    @Test
    @DisplayName("CP-14 | nombre con 151 caracteres (sobre el límite) genera violación")
    void nombre_sobreLongitudMaxima_debeGenerarViolacion() {
        Producto producto = productoValido();
        producto.setNombre("A".repeat(151)); // supera @Size(max=150)

        Set<ConstraintViolation<Producto>> violaciones = validator.validate(producto);

        assertThat(violaciones).anyMatch(v -> v.getPropertyPath().toString().equals("nombre"));
    }

    // ================================================================
    // 4. RELACIONES CON OTRAS ENTIDADES
    // ================================================================

    @Test
    @DisplayName("CP-15 | Producto acepta categoria y proveedor como null (son opcionales en BD)")
    void categoria_y_proveedor_pueden_ser_nulos() {
        Producto producto = productoValido();
        producto.setCategoria(null);
        producto.setProveedor(null);

        // La entidad no tiene @NotNull en categoria ni proveedor
        Set<ConstraintViolation<Producto>> violaciones = validator.validate(producto);

        assertThat(violaciones).isEmpty();
    }

    @Test
    @DisplayName("CP-16 | Producto con categoria asignada retorna la misma instancia")
    void categoria_asignada_debeRetornarMismaInstancia() {
        Categoria cat = Categoria.builder().id(5L).nombre("Herramientas").build();
        Producto producto = productoValido();
        producto.setCategoria(cat);

        assertThat(producto.getCategoria()).isSameAs(cat);
        assertThat(producto.getCategoria().getId()).isEqualTo(5L);
    }

    // ================================================================
    // Método auxiliar – Producto mínimo válido para reutilizar en tests
    // ================================================================
    private Producto productoValido() {
        return Producto.builder()
                .nombre("Producto de Prueba")
                .precio(99.99)
                .stock(10)
                .activo(true)
                .build();
    }
}
