# 🐾 PetRent — Plataforma de Alquiler de Perros

Aplicación web que permite a los usuarios explorar un catálogo de perros, realizar reservas por horas, dejar reseñas y gestionar su cuenta. Incluye un panel de administración completo y un asistente de inteligencia artificial con Ollama.

---

## Tecnologías

| Capa | Tecnología |
|---|---|
| Frontend | Svelte 4 + Vite + svelte-routing |
| Backend | Spring Boot 3.5 + Spring Security + Spring Data JPA |
| Base de datos | PostgreSQL |
| IA local | Ollama (llama3.2:3b) |
| Documentación API | Swagger / OpenAPI 3 |
| Build tool | Maven |

---

## Estructura del repositorio

```
proyecto-integrador/
├── petrent/              ← Frontend (Svelte)
├── petrent-api/          ← Backend (Spring Boot)
│   └── database/
│       ├── 01_tablas.sql
│       └── 02_datos.sql
```

---

## Requisitos previos

- Node.js 24
- Java 25
- Maven 3.9+
- PostgreSQL 18
- Ollama instalado

---

## Configuración de la base de datos

**1.** Crear la base de datos y el rol con `01_tablas.sql`.

**2.** Ejecutar los scripts en orden: `02_datos.sql`.

### Tablas del sistema

| Tabla | Descripción |
|---|---|
| `usuarios` | Cuentas de usuario con roles (`admin` / `usuario`) y eliminación lógica |
| `perros` | Catálogo de perros disponibles para alquilar |
| `reservas` | Reservas por horas vinculadas a usuario y perro |
| `pagos` | Registro de pagos asociados a cada reserva |
| `resenas` | Calificaciones y comentarios por reserva completada |

---

## Configuración del backend

**1.** Crear el archivo `.env` en la carpeta del backend:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=petrent_db
DB_USERNAME=petrent_app
DB_PASSWORD=CHANGE_ME
APP_ADMIN_USERNAME=admin
APP_ADMIN_PASSWORD=CHANGE_ME
APP_ADMIN_EMAIL=admin@petrent.com
APP_CORS_ORIGENES=http://localhost:5173
OLLAMA_URL=http://localhost:11434
OLLAMA_MODELO=llama3.2:3b
```

**2.** Ejecutar el backend:

```bash
./mvnw spring-boot:run
```

El backend arranca en `http://localhost:8080`. Al iniciar, `AdminInitializer` crea automáticamente la cuenta admin si no existe.

---

## Configuración del frontend

**1.** Instalar dependencias:

```bash
npm install
```

**2.** Iniciar el servidor de desarrollo:

```bash
npm run dev
```

El frontend queda disponible en `http://localhost:5173`.

La URL del backend se configura en `src/config.js`:

```js
export const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080/api";
```

---

## Configuración de Ollama (IA local)

**1.** Descargar el modelo:

```bash
ollama pull llama3.2:3b
```

**2.** Verificar que el servidor esté activo en `http://localhost:11434`.

Ollama se inicia automáticamente al arrancar Windows. Si no está corriendo: `ollama serve`.

---

## Endpoints principales

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| POST | `/api/auth/registro` | Público | Registrar nueva cuenta |
| GET | `/api/auth/me` | Autenticado | Verificar sesión activa |
| GET | `/api/perros` | Público | Listar perros (paginado) |
| GET | `/api/perros/buscar` | Público | Buscar por nombre o raza |
| POST | `/api/perros` | ADMIN | Crear perro |
| PUT | `/api/perros/{id}` | ADMIN | Editar perro |
| DELETE | `/api/perros/{id}` | ADMIN | Dar de baja (lógica) |
| GET | `/api/usuarios` | ADMIN | Listar usuarios |
| GET | `/api/reservas` | ADMIN | Listar todas las reservas |
| POST | `/api/reservas` | Autenticado | Crear reserva |
| POST | `/api/ia/consulta` | Público | Consultar asistente IA |

Documentación interactiva completa en: `http://localhost:8080/swagger-ui.html`

---

## Autenticación

El sistema usa **HTTP Basic Authentication**. Cada petición protegida envía las credenciales en el header:

```
Authorization: Basic <base64(correo:password)>
```

El frontend codifica la credencial con `btoa(correo:password)` y la almacena en `localStorage` junto con los datos de sesión. Al cerrar sesión, se limpia el store y el `localStorage`.

### Roles

| Rol | Acceso |
|---|---|
| `admin` | Panel de administración, CRUD completo de perros y usuarios, gestión de reservas y pagos |
| `usuario` | Catálogo, reservas propias, reseñas, perfil |

---

## Rutas del frontend

| Ruta | Componente | Acceso |
|---|---|---|
| `/` | `Inicio.svelte` | Público |
| `/login` | `Login.svelte` | Público |
| `/registro` | `Registro.svelte` | Público |
| `/perros` | `Catalogo.svelte` | Autenticado |
| `/perros/:id` | `Detalle.svelte` | Autenticado |
| `/carrito` | `Carrito.svelte` | Autenticado |
| `/confirmado` | `Confirmado.svelte` | Autenticado |
| `/mi-cuenta` | `MiCuenta.svelte` | Autenticado |
| `/admin` | `Admin.svelte` | Solo ADMIN |
| `/ia` | `AsistenteIA.svelte` | Público |

La protección de rutas se implementa en `RutaProtegida.svelte`, que lee el store `sesion` y redirige a `/login` si no hay sesión activa. Las rutas de solo admin también verifican el campo `rol`.

---

## Asistente IA

El endpoint `POST /api/ia/consulta` recibe una pregunta, consulta el estado actual de la base de datos (usuarios, perros, reservas, reseñas) y construye un contexto dinámico que se envía junto con la pregunta al modelo local de Ollama.

Ejemplo de petición:

```json
{
  "consulta": "¿Cuántos perros están disponibles ahora mismo?"
}
```

Respuesta:

```json
{
  "respuesta": "Actualmente hay 4 perros disponibles para alquiler...",
  "modelo": "llama3.2:3b"
}
```

---

## Usuarios de prueba

| Correo | Contraseña | Rol |
|---|---|---|
| `jeanpierre@gmail.com` | `123456` | usuario |

Los usuarios cliente se registran desde `/registro`.

---

## Evolución del proyecto

En etapas anteriores el frontend cargaba los datos desde archivos JSON locales:

```js
// Antes
fetch('/datos/perros.json')
```

En esta etapa final, todos los datos provienen de la API REST conectada a PostgreSQL:

```js
// Ahora
fetch('http://localhost:8080/api/perros')
```
