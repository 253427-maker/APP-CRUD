Control de Cultivos — App CRUD en Android

Aplicación móvil nativa en Android desarrollada en Kotlin con Jetpack Compose, arquitectura MVVM, Capa de Datos limpia (Repository, DTOs, Mappers), Inyección de Dependencias Manual y autenticación basada en Tokens JWT (Bearer).

Proyecto: Unidad 3 — Desarrollo de Aplicaciones Móviles  
Universidad: Universidad Politécnica de Chiapas (UP Chiapas)  
Carrera: Ingeniería en Tecnologías de la Información e Innovación Digital  
Alumna: Karen Lizeth Pérez Hernández  
Repositorio oficial: [https://github.com/253427-maker/APP-CRUD.git](https://github.com/253427-maker/APP-CRUD.git)

---

Tabla de Contenidos
1. [Descripción del Dominio](#-descripción-del-dominio)
2. [Stack Tecnológico](#-stack-tecnológico)
3. [Arquitectura y Organización de Paquetes](#-arquitectura-y-organización-de-paquetes)
4. [Inyección de Dependencias Manual (DI)](#-inyección-de-dependencias-manual-di)
5. [Backend de Pruebas (json-server-auth)](#-backend-de-pruebas-json-server-auth)
6. [Guía de Ejecución Rápida](#-guía-de-ejecución-rápida)
7. [Matriz de Cumplimiento de Requerimientos (RF-01 a RF-10)](#-matriz-de-cumplimiento-de-requerimientos)
8. [Autoevaluación contra la Rúbrica (40 Puntos)](#-autoevaluación-contra-la-rúbrica)

---

Descripción del Dominio

El dominio seleccionado es el Control y Gestión de Cultivos Agrícolas.  
El recurso principal del sistema es **`Parcela`** (ruta API: `/parcelas`).

Atributos del Modelo `Parcela`:
| Atributo | Tipo de Dato | Restricción / Validación | Descripción |
|---|---|---|---|
| `id` | `Int` | Asignado automáticamente por el backend | Identificador numérico único de la parcela |
| `nombre` | `String` | Obligatorio, mínimo 3 caracteres | Nombre o denominación del terreno o lote |
| `cultivo` | `String` | Obligatorio, no vacío | Tipo de cultivo sembrado (ej. Maíz, Frijol, Café) |
| `hectareas` | `Double` | Obligatorio, número decimal > 0.0 | Extensión territorial en hectáreas |
| `fechaSiembra` | `String` | Obligatorio, formato estricto `yyyy-MM-dd` | Fecha de inicio de la siembra |
| `riegoActivo` | `Boolean` | Booleano (`true` / `false`) | Indica si cuenta con sistema de riego automatizado |

Nota de diseño (YAGNI): Cumpliendo con los requisitos del proyecto, se modela un único recurso principal sin relaciones entre entidades, enfocado en la robustez arquitectónica y separación de capas.

---

Stack Tecnológico

El proyecto sigue estrictamente el stack moderno de Android recomendado por Google y solicitado en el documento de especificación:

- **Lenguaje:** Kotlin 2.4.21 (JVM 21)
- **UI:** 100% Jetpack Compose + Material 3 (cero layouts XML de pantallas)
- **Navegación:** Navigation Compose (`NavHost`, rutas tipadas y gestión segura del back stack con `popUpTo`)
- **Arquitectura:** MVVM (Model-View-ViewModel) + StateFlow/UiState inmutable observado con `collectAsStateWithLifecycle()`
- **Concurrencia:** Corrutinas de Kotlin (`viewModelScope`, funciones `suspend`)
- **Red:** Retrofit 3.0.0 + OkHttp 4.12.0 + `retrofit2-kotlinx-serialization-converter`
- **Serialización:** `kotlinx.serialization` con DTOs anotados con `@Serializable`
- **Persistencia de Sesión:** AndroidX DataStore Preferences encapsulado bajo la interfaz `SessionStorage`
- **Inyección de Dependencias:** **DI Manual** mediante `AppContainer` a nivel de `Application` + inyección por constructor + `ViewModelProvider.Factory` con `viewModelFactory { initializer { } }` (sin Hilt, sin Dagger, sin Koin).
- **Gestión de Versiones:** Gradle Version Catalog (`gradle/libs.versions.toml`)
- **Control de Versiones:** Git + GitHub

---

Arquitectura y Organización de Paquetes

La aplicación está organizada por features (características) y subdividida en capas limpias (`domain`, `data`, `ui`):

```text
com.upchiapas.appcrud
├── MyApp.kt                         # Application: inicializa el AppContainer singleton
├── MainActivity.kt                  # Activity principal: inicializa Compose y AppNavHost
├── AppContainer.kt                  # Contenedor de dependencias manual (Singletons)
│
├── core/
│   ├── network/
│   │   ├── AuthInterceptor.kt       # OkHttp Interceptor: inyecta "Authorization: Bearer <token>"
│   │   └── HostFallbackInterceptor.kt # Resiliencia: conmuta entre 10.0.2.2 y 127.0.0.1
│   └── result/
│       └── ResultMapper.kt          # Mapeo unificado de excepciones de red a mensajes amigables
│
├── features/
│   ├── auth/
│   │   ├── data/
│   │   │   ├── AuthApiService.kt    # Interfaz Retrofit: POST /login, POST /register
│   │   │   ├── AuthRepositoryImpl.kt# Implementación de AuthRepository (consume API y guarda token)
│   │   │   ├── SessionStorageImpl.kt# Implementación de SessionStorage con DataStore Preferences
│   │   │   ├── dto/                 # DTOs @Serializable de request/response
│   │   │   │   ├── LoginRequestDto.kt
│   │   │   │   ├── LoginResponseDto.kt
│   │   │   │   └── RegisterRequestDto.kt
│   │   │   └── mapper/
│   │   │       └── AuthMapper.kt    # Funciones de extensión DTO -> Dominio (Session, User)
│   │   ├── domain/
│   │   │   ├── model/               # Modelos puros de dominio (User, Session)
│   │   │   │   ├── User.kt
│   │   │   │   └── Session.kt
│   │   │   ├── repository/
│   │   │   │   └── AuthRepository.kt# Contrato (interfaz) de autenticación
│   │   │   └── SessionStorage.kt    # Contrato (interfaz) para persistencia de sesión
│   │   └── ui/
│   │       ├── LoginScreen.kt       # Pantalla de Login (Compose)
│   │       ├── LoginUiState.kt      # Data class inmutable de estado de Login
│   │       ├── LoginViewModel.kt    # ViewModel con validaciones y gestión de estado
│   │       ├── RegisterScreen.kt    # Pantalla de Registro (Compose)
│   │       ├── RegisterUiState.kt   # Data class inmutable de estado de Registro
│   │       └── RegisterViewModel.kt # ViewModel con validación de 4 campos
│   │
│   └── parcelas/
│       ├── data/
│       │   ├── ParcelaApiService.kt # Interfaz Retrofit: CRUD completo en /parcelas
│       │   ├── ParcelaRepositoryImpl.kt # Implementación de ParcelaRepository
│       │   ├── dto/                 # DTOs @Serializable de transporte de datos
│       │   │   ├── ParcelaDto.kt
│       │   │   └── ParcelaRequestDto.kt
│       │   └── mapper/
│       │       └── ParcelaMapper.kt # Mappers DTO <-> Parcela de dominio
│       ├── domain/
│       │   ├── model/
│       │   │   └── Parcela.kt       # Modelo de dominio de la entidad Parcela
│       │   └── repository/
│       │       └── ParcelaRepository.kt # Contrato (interfaz) del repositorio CRUD
│       └── ui/
│           ├── ParcelaListScreen.kt # Pantalla Home: lista, estados carga/vacío/error, diálogo eliminar
│           ├── ParcelaListUiState.kt# Estado inmutable de la lista
│           ├── ParcelaListViewModel.kt # ViewModel de la lista y cierre de sesión
│           ├── ParcelaDetailScreen.kt # Pantalla de detalle con todos los atributos
│           ├── ParcelaFormScreen.kt # Formulario reutilizable (crear y editar)
│           ├── ParcelaFormUiState.kt# Estado inmutable de campos de formulario (sobrevive a rotación)
│           └── ParcelaFormViewModel.kt # ViewModel reutilizable con validaciones de campos
│
├── navigation/
│   ├── AppNavHost.kt                # Grafo de navegación y lógica de inicio según sesión
│   └── NavRoutes.kt                 # Definición de rutas centralizadas
│
└── ui/theme/
    └── Theme.kt                     # Tema Material 3
```

---

Inyección de Dependencias Manual (DI)

En apego estricto a la rúbrica (**cero librerías de DI** como Hilt o Koin):
1. **Contenedor Único (`AppContainer`):** Se instancia una sola vez dentro de la clase `MyApp : Application()` al arrancar el proceso.
2. **Instancias Compartidas:**
   - Una única instancia de `OkHttpClient` configurada con `AuthInterceptor` y `HttpLoggingInterceptor`.
   - Una única instancia de `Retrofit` con el convertidor de `kotlinx.serialization`.
   - Una única instancia de `SessionStorage` (DataStore).
   - Instancias únicas de `AuthRepository` y `ParcelaRepository`.
3. **Inyección por Constructor:** Todos los repositorios, servicios e interceptores reciben sus dependencias por constructor tipadas **exclusivamente como interfaces**.
4. **Factories de ViewModel:** Cada ViewModel provee una factory mediante el DSL estándar `viewModelFactory { initializer { ... } }`, desacoplando la creación de las vistas.

---

Backend de Pruebas (json-server-auth)

El proyecto incluye su propio backend local en la carpeta `/backend`:

- **Herramientas:** `json-server 0.17.4` + `json-server-auth 2.1.0`.
- **Ruta de rutas (`routes.json`):** `/parcelas* -> /660/parcelas$1` (requiere token JWT tanto para lectura como para escritura).
- **Base de datos (`db.json`):** Usuarios pre-registrados con contraseñas encriptadas con `bcrypt` y 5 parcelas iniciales de muestra.

### Contrato de la API:
| Método | Endpoint | Protección | Descripción |
|---|---|---|---|
| `POST` | `/register` | Pública | Registra un usuario y retorna `accessToken` + `user` |
| `POST` | `/login` | Pública | Inicia sesión y retorna `accessToken` + `user` |
| `GET` | `/parcelas` | `Bearer Token` | Lista todas las parcelas |
| `POST` | `/parcelas` | `Bearer Token` | Crea una nueva parcela |
| `GET` | `/parcelas/{id}` | `Bearer Token` | Obtiene el detalle de una parcela |
| `PUT` | `/parcelas/{id}` | `Bearer Token` | Actualiza todos los campos de una parcela |
| `DELETE` | `/parcelas/{id}` | `Bearer Token` | Elimina una parcela |

**Credenciales de prueba iniciales:**
**Correo:** `karen@upchiapas.edu.mx`
**Contraseña:** `12345678`
