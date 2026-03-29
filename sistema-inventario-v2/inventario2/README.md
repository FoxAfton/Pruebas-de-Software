# Sistema de Inventario — Spring Boot + JWT + H2

## Tecnologías
- Java 17
- Spring Boot 3.2.4
- Spring Security + JWT (jjwt 0.11.5)
- Spring Data JPA
- H2 Database (archivo local, no requiere instalación)
- Lombok
- JUnit 5 + Mockito

---

## Requisitos previos
- Java 17+
- Maven 3.8+

---

## Cómo ejecutar

```bash
# 1. Entrar a la carpeta del proyecto
cd sistema-inventario

# 2. Compilar y ejecutar
./mvnw spring-boot:run
# En Windows:
mvnw.cmd spring-boot:run
```

La aplicación arranca en **http://localhost:8080**

### Usuario admin por defecto
| Campo    | Valor                  |
|----------|------------------------|
| Email    | admin@inventario.com   |
| Password | Admin123!              |
| Rol      | ADMIN                  |

### Consola H2 (base de datos)
http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:file:./data/inventario`
- User: `sa` / Password: *(vacío)*

---

## Endpoints principales

### Autenticación (públicos)
| Método | Ruta                | Descripción              |
|--------|---------------------|--------------------------|
| POST   | /api/auth/login     | Obtener token JWT        |
| POST   | /api/auth/registro  | Registrar nuevo usuario  |

**Ejemplo login:**
```json
POST /api/auth/login
{
  "email": "admin@inventario.com",
  "password": "Admin123!"
}
```
Respuesta:
```json
{
  "token": "eyJ...",
  "email": "admin@inventario.com",
  "nombre": "Administrador",
  "rol": "ADMIN"
}
```

### Endpoints protegidos (requieren `Authorization: Bearer <token>`)
| Método | Ruta                          | Descripción                        |
|--------|-------------------------------|------------------------------------|
| GET    | /api/categorias               | Listar categorías                  |
| POST   | /api/categorias               | Crear categoría                    |
| PUT    | /api/categorias/{id}          | Actualizar categoría               |
| DELETE | /api/categorias/{id}          | Eliminar categoría                 |
| GET    | /api/proveedores              | Listar / buscar proveedores        |
| POST   | /api/proveedores              | Crear proveedor                    |
| PUT    | /api/proveedores/{id}         | Actualizar proveedor               |
| DELETE | /api/proveedores/{id}         | Eliminar proveedor                 |
| GET    | /api/productos                | Listar productos (paginado)        |
| GET    | /api/productos/{id}           | Obtener producto por ID            |
| GET    | /api/productos/stock-bajo     | Productos con stock bajo           |
| POST   | /api/productos                | Crear producto                     |
| PUT    | /api/productos/{id}           | Actualizar producto                |
| DELETE | /api/productos/{id}           | Baja lógica de producto            |
| GET    | /api/movimientos?productoId=X | Historial de movimientos           |
| POST   | /api/movimientos              | Registrar entrada/salida           |

---

## Ejecutar pruebas

```bash
./mvnw test
# En Windows:
mvnw.cmd test
```

### Pruebas incluidas

| Archivo | Tipo | Casos |
|---------|------|-------|
| CategoriaServiceTest | Unitaria | 5 |
| MovimientoServiceTest | Unitaria | 4 |
| ProductoServiceTest | Unitaria | 3 |
| AuthControllerIntegrationTest | Integración | 5 |
| CategoriaControllerIntegrationTest | Integración | 5 |
| ProductoControllerIntegrationTest | Integración | 5 |
| MovimientoControllerIntegrationTest | Integración | 4 |

**Total: 31 casos de prueba**

---

## Estructura del proyecto

```
src/
├── main/java/com/inventario/
│   ├── config/          # SecurityConfig, DataInitializer
│   ├── controller/      # AuthController, CategoriaController, ProductoController,
│   │                      ProveedorController, MovimientoController
│   ├── dto/             # AuthDtos, InventarioDtos
│   ├── exception/       # GlobalExceptionHandler, excepciones personalizadas
│   ├── model/           # Categoria, Producto, Proveedor, MovimientoInventario, Usuario
│   ├── repository/      # Interfaces JPA
│   ├── security/        # JwtUtils, JwtAuthFilter, CustomUserDetailsService
│   └── service/         # CategoriaService, ProductoService,
│                          ProveedorService, MovimientoService
└── test/java/com/inventario/
    ├── controller/      # Pruebas de integración (MockMvc)
    └── service/         # Pruebas unitarias (Mockito)
```
