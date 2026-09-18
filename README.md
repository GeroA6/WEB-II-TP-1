# TP1 · Spring Boot, API REST y arquitectura en capas

Resolución del Trabajo Práctico 1 para la materia **Web II**.  
El proyecto implementa una API REST modular construida sobre **Spring Boot 4.1.x** y **Java 25**, aplicando una arquitectura en capas (**Controller → Service → Repository / Client**), desacoplamiento mediante DTOs, validación con Bean Validation, manejo centralizado de errores (`ProblemDetail` RFC 7807) y documentación con Swagger / OpenAPI.

---

## Cómo levantar el proyecto

Requiere Java 25. Usar siempre el wrapper de Maven (`mvnw`):

```bash
# Windows
.\mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

Cuando el log muestre `Started DemoApplication`, la API quedará escuchando en `http://localhost:8080`.

Para compilar y correr los tests:

```bash
# Windows
.\mvnw.cmd test

# macOS / Linux
./mvnw test
```

---

## Documentación interactiva (Swagger UI)

Una vez levantada la aplicación, podés consultar y probar todos los endpoints de forma interactiva en Swagger UI navegando a:

**[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**  
*(o alternativamente `http://localhost:8080/swagger-ui.html`)*

La documentación organiza los endpoints en dos grupos principales mediante `@Tag`:
- `productos`: Catálogo de solo lectura que consume una API externa.
- `favoritos`: Recurso propio con CRUD completo en memoria.

---

## Endpoints de la API

### 1. Utilidades y Health Check
| Método | Path | Qué hace | Código HTTP |
|---|---|---|---|
| GET | `/health` | Chequeo de salud del servicio | 200 OK |
| GET | `/ping` | Chequeo trivial (devuelve `pong`) | 200 OK |

### 2. Catálogo de Productos (Consumo externo a DummyJSON)
| Método | Path | Qué hace | Códigos HTTP |
|---|---|---|---|
| GET | `/api/productos` | Lista todos los productos mapeados a `ProductoDTO` | 200 OK |
| GET | `/api/productos/{id}` | Obtiene un producto por su ID | 200 OK / 404 Not Found |

### 3. Favoritos (CRUD propio en memoria)
| Método | Path | Qué hace | Códigos HTTP |
|---|---|---|---|
| POST | `/api/favoritos` | Crea un favorito (valida campos obligatorios) | 201 Created (+ Location) / 400 Bad Request |
| GET | `/api/favoritos` | Lista todos los favoritos | 200 OK |
| GET | `/api/favoritos/{id}` | Obtiene un favorito por su ID | 200 OK / 404 Not Found |
| PUT | `/api/favoritos/{id}` | Actualiza un favorito (conserva `fechaAgregado`) | 200 OK / 400 Bad Request / 404 Not Found |
| DELETE | `/api/favoritos/{id}` | Elimina un favorito por su ID | 204 No Content / 404 Not Found |

---

## Colección de Pruebas (`requests.http`)

En la raíz del proyecto se incluye el archivo `requests.http`, que contiene peticiones listas para ejecutar con la extensión **REST Client** de VS Code o **HTTP Client** de IntelliJ.

Incluye pruebas para:
- **Casos de éxito:**
  - Consumo y mapeo de productos.
  - Creación de favorito con retorno de `201 Created` y cabecera `Location`.
  - Lectura, actualización y eliminación de favoritos.
- **Casos de error:**
  - `404 Not Found`: Búsqueda, actualización o eliminación de IDs inexistentes (en productos y favoritos).
  - `400 Bad Request`: Validación fallida al enviar campos en blanco o nulos en `FavoritoRequest`.

---

## Arquitectura y Decisiones de Diseño

1. **Desacoplamiento con DTOs (`records`):**
   - La API no expone el modelo tal cual lo devuelve DummyJSON (`DummyJsonProducto`), sino un DTO propio (`ProductoDTO`).
   - Para favoritos se separó la entrada (`FavoritoRequest`, con validaciones `@NotNull` y `@NotBlank`) de la salida (`FavoritoResponse`), protegiendo la inmutabilidad y evitando manipulación indebida de `id` o `fechaAgregado`.
2. **Repositorio en Memoria Thread-Safe:**
   - La implementación `InMemoryFavoritoRepository` utiliza `ConcurrentHashMap` y `AtomicLong` para garantizar consistencia ante múltiples peticiones concurrentes en Tomcat.
3. **Manejo Centralizado de Excepciones:**
   - Mediante `@RestControllerAdvice` (`GlobalExceptionHandler`), todos los errores se traducen a respuestas estándar bajo la especificación **ProblemDetail (RFC 7807)** con código de estado, título y detalle.
