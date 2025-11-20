# 📋 DIAGNÓSTICO COMPLETO DE VIOLACIONES SOLID
## Backend SIGC - Análisis Profesional

---

## 📊 RESUMEN EJECUTIVO

El backend actual viola **4 de 5 principios SOLID** de manera crítica:

| Principio | Severidad | Archivos Afectados | Estado |
|-----------|-----------|------------------|--------|
| **SRP** (Single Responsibility) | 🔴 CRÍTICA | AuthController, CitaController, UsuarioController, DoctorController | ❌ Múltiples responsabilidades |
| **OCP** (Open/Closed) | 🔴 CRÍTICA | Todos los controllers | ❌ Rígidos a extensión |
| **LSP** (Liskov Substitution) | 🟠 MEDIA | Repositories, Services | ⚠️ Inconsistencias |
| **ISP** (Interface Segregation) | 🟡 BAJA | SecurityConfig, JwtUtil | ⚠️ Interfaces sobrecargadas |
| **DIP** (Dependency Inversion) | 🔴 CRÍTICA | Controllers usan @Autowired | ❌ Dependen de implementaciones |

**Impacto actual:**
- ❌ Difícil de testear (lógica acoplada a HTTP)
- ❌ Difícil de mantener (responsabilidades mezcladas)
- ❌ Difícil de extender (código rígido con muchos condicionales)
- ❌ Acoplamiento fuerte entre capas

---

## 🔴 VIOLACIÓN 1: SRP - SINGLE RESPONSIBILITY PRINCIPLE

### Definición del Principio
Cada clase debe tener **una sola razón para cambiar**.

### Violaciones Encontradas

#### 1.1 AuthController - 6 responsabilidades diferentes

**Archivo:** `src/main/java/com/sigc/backend/controller/AuthController.java`

**Responsabilidades actuales:**
1. ✅ Manejo HTTP (RestController)
2. ❌ Validación de credenciales (líneas 66-92)
3. ❌ Encriptación de contraseñas (línea 76)
4. ❌ Generación de tokens JWT (línea 77)
5. ❌ Consultas a base de datos (línea 68, 73)
6. ❌ Lógica de cambio de contraseña completa (líneas 135-293)

**Código problemático:**
```java
// ❌ PROBLEMA: Controller hace TODO
@PostMapping("/login")
public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
    // 1. Extrae credenciales (HTTP)
    String email = credentials.get("email");
    String password = credentials.get("password");
    
    // 2. Consulta BD
    Usuario usuario = usuarioRepository.findByEmail(email);
    
    // 3. Valida credenciales
    if (usuario != null && passwordEncoder.matches(password, usuario.getPassword())) {
        // 4. Genera token
        String token = jwtUtil.generateToken(usuario.getIdUsuario(), ...);
        
        // 5. Construye respuesta HTTP
        response.put("message", "Login exitoso");
        // ...
    }
}
```

**Debería ser:**
```java
// ✅ SOLUCIÓN: Controller solo maneja HTTP
@PostMapping("/login")
public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
    // Delega a servicio
    LoginResponse response = authService.autenticar(request);
    return ResponseEntity.ok(response);
}
```

#### 1.2 CitaController - 7 responsabilidades

**Archivo:** `src/main/java/com/sigc/backend/controller/CitaController.java`

**Responsabilidades actuales:**
1. ✅ Manejo HTTP
2. ❌ Validación de autorización (JWT)
3. ❌ 8 validaciones de negocio (líneas 113-173)
4. ❌ Modificación de estados (disponibilidad de horarios)
5. ❌ Lógica de transacciones
6. ❌ Consultas múltiples a repositorios
7. ❌ Mapeo de datos (líneas 157-165)

**Código problemático:**
```java
// ❌ PROBLEMA: 8 validaciones en el controller
if (!jwtUtil.validateToken(token)) { /* Error */ }
if (cita.getUsuario() == null) { /* Error */ }
if (cita.getHorario() == null) { /* Error */ }
Usuario usuario = usuarioRepository.findById(...)
Horario horario = horarioRepository.findById(...)
if (!horario.isDisponible()) { /* Error */ }
List<Cita> citasExistentes = citaRepository.findByHorario_IdHorario(...)
if (horarioDateTime.isBefore(ahora)) { /* Error */ }

// Toda la lógica de creación
cita.setUsuario(usuario);
horario.setDisponible(false);
citaRepository.save(cita);
```

#### 1.3 UsuarioController - Mezcla de CRUD + Cambio de Contraseña

**Archivo:** `src/main/java/com/sigc/backend/controller/UsuarioController.java`

**Responsabilidades actuales:**
1. ✅ Manejo HTTP
2. ❌ CRUD genérico (líneas 28-111)
3. ❌ Lógica de cambio de contraseña (líneas 118-291)
4. ❌ Validaciones duplicadas con AuthController

**Problema:** El método `cambiarPassword` en líneas 118-291 duplica completamente la lógica en `AuthController.cambiarPasswordImpl()` - **violación de DRY + SRP**.

#### 1.4 DoctorController - Mezcla de CRUD + Gestión de Archivos

**Archivo:** `src/main/java/com/sigc-backend/controller/DoctorController.java`

**Responsabilidades actuales:**
1. ✅ Manejo HTTP
2. ❌ Validación de archivos (líneas 132-148)
3. ❌ Guardado de archivos en sistema de archivos
4. ❌ Lógica de directorios
5. ❌ Servicio de archivos estáticos

### Impacto SRP

- **Testabilidad:** Imposible testear lógica sin servidor HTTP mock
- **Reusabilidad:** No se puede usar lógica de negocio desde CLI, trabajos programados, etc.
- **Mantenibilidad:** Un cambio en validación afecta múltiples controllers

---

## 🔴 VIOLACIÓN 2: OCP - OPEN/CLOSED PRINCIPLE

### Definición del Principio
Las clases deben estar **abiertas para extensión, cerradas para modificación**.

### Violaciones Encontradas

#### 2.1 Validaciones hardcodeadas en cada endpoint

**Archivo:** `AuthController.java` líneas 135-293, `UsuarioController.java` líneas 118-291

**Código problemático:**
```java
// ❌ PROBLEMA: Cada endpoint repite las mismas 7 validaciones manualmente
// AuthController.cambiarPasswordImpl()
if (authHeader == null || authHeader.isEmpty()) { }
if (!jwtUtil.validateToken(token)) { }
if (request.getPasswordActual() == null) { }
if (!passwordEncoder.matches(...)) { }
if (!request.getPasswordNueva().equals(...)) { }
// ... 3 validaciones más

// UsuarioController.cambiarPassword()
// ❌ DUPLICADAS: Exactamente las mismas 7 validaciones
if (authHeader == null || authHeader.isEmpty()) { }
if (!jwtUtil.validateToken(token)) { }
if (request.getPasswordActual() == null) { }
if (!passwordEncoder.matches(...)) { }
if (!request.getPasswordNueva().equals(...)) { }
// ... 3 validaciones más
```

**Impacto OCP:** 
- Si queremos agregar una nueva validación de contraseña, debemos modificar 2 lugares
- Si agregamos un nuevo endpoint, debemos repetir TODO de nuevo
- **No está abierto a extensión** (no hay forma de reutilizar validaciones)

#### 2.2 Creación de citas con 8 validaciones inline

**Archivo:** `CitaController.java` líneas 113-173

**Código problemático:**
```java
// ❌ PROBLEMA: Lógica de validación rígida en endpoint
// Si queremos validar lo mismo en otro lugar, hay que copiar TODO

if (!jwtUtil.validateToken(token)) { /* Error */ }
if (cita.getUsuario() == null) { /* Error */ }
if (cita.getHorario() == null) { /* Error */ }
Usuario usuario = usuarioRepository.findById(...)
Horario horario = horarioRepository.findById(...)
if (!horario.isDisponible()) { /* Error */ }
List<Cita> citasExistentes = citaRepository.findByHorario_IdHorario(...)
if (!citasExistentes.isEmpty()) { /* Error */ }
if (horarioDateTime.isBefore(ahora)) { /* Error */ }
```

**¿Cómo extendemos esto?**
- ¿Queremos validar lo mismo al actualizar una cita? → Duplicar TODO
- ¿Queremos validar desde otra fuente (API interna, CLI)? → Duplicar TODO
- ¿Queremos reutilizar con otra entidad? → Imposible

#### 2.3 Condicionales rígidas para tipos de usuario

**Archivo:** `SecurityConfig.java` líneas 40-50

**Código problemático:**
```java
// ❌ PROBLEMA: Hardcoded, no es fácil agregar nuevos roles
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/auth/**").permitAll()
    .requestMatchers("/doctores/**").permitAll()
    .requestMatchers("/citas/**").permitAll()
    // Si queremos agregar /consultas/**, debemos modificar este archivo
    // Si queremos usar roles diferentes (ADMIN, DOCTOR, PACIENTE), no hay forma
```

#### 2.4 Respuestas de error hardcodeadas

**Archivo:** Todos los controllers

**Código problemático:**
```java
// ❌ PROBLEMA: Cada controller crea su propio mapa de errores
Map<String, Object> error = new HashMap<>();
error.put("error", mensaje);
error.put("timestamp", LocalDateTime.now());
// ¿Y si queremos agregar un campo más? Modificar 10 controllers

// ❌ También en CitaController:
return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(crearError("Token JWT requerido en header Authorization"));
// Duplicado en 3 controllers diferentes
```

### Impacto OCP

- **Extensibilidad:** Cada nueva característica requiere modificar código existente
- **Confiabilidad:** Riesgo alto de regresiones al duplicar código
- **Mantenibilidad:** Los cambios deben propagarse manualmente a múltiples ubicaciones

---

## 🟠 VIOLACIÓN 3: LSP - LISKOV SUBSTITUTION PRINCIPLE

### Definición del Principio
Los objetos derivados **pueden sustituir a sus bases** sin alterar el comportamiento esperado.

### Violaciones Encontradas

#### 3.1 Inconsistencia en tipos de respuesta

**Archivo:** Todos los controllers

**Código problemático:**
```java
// ❌ PROBLEMA: Inconsistencia en retorno de tipos
// AuthController.login() - devuelve Map<String, Object>
public ResponseEntity<Map<String, Object>> login(...)

// UsuarioController.listarUsuarios() - devuelve List directa
public List<Usuario> listarUsuarios()

// CitaController.listar() - devuelve List directa
public List<Cita> listar()

// MeController.obtenerUsuarioAutenticado() - devuelve Map
public ResponseEntity<?> obtenerUsuarioAutenticado(...)

// ¿Cuál es la interfaz esperada? INCONSISTENCIA
```

**Debería ser:**
```java
// ✅ SOLUCIÓN: Interfaz consistente
public ResponseEntity<ApiResponse<UsuarioDto>> login(...)
public ResponseEntity<ApiResponse<List<UsuarioDto>>> listarUsuarios(...)
public ResponseEntity<ApiResponse<CitaDto>> obtenerCita(...)
```

#### 3.2 Manejo de errores inconsistente

**Archivo:** Varios controllers

**Código problemático:**
```java
// ❌ PROBLEMA: Diferentes códigos HTTP para el mismo error
// AuthController
return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(...)

// CitaController
return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(...)

// ¿Cuál usar para "usuario no autenticado"? No hay estándar
```

#### 3.3 DTO con métodos estáticos para crear instancias

**Archivo:** `CambiarPasswordResponse.java`

**Código problemático:**
```java
// Si la clase usa método estático y luego cambiamos a constructor normal
// O si usamos herencia, el método estático no se puede sobrescribir
// Violación potencial de LSP en caso de extensión
```

### Impacto LSP

- **Predecibilidad:** El cliente no sabe qué esperar de cada endpoint
- **Consistencia:** Difícil mantener patrones uniformes

---

## 🟡 VIOLACIÓN 4: ISP - INTERFACE SEGREGATION PRINCIPLE

### Definición del Principio
Los clientes **no deben depender de interfaces que no usan**.

### Violaciones Encontradas

#### 4.1 Repositorio JpaRepository

**Archivo:** `UsuarioRepository.java`, `CitaRepository.java`, etc.

**Código problemático:**
```java
// ❌ PROBLEMA: JpaRepository expone demasiados métodos
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmail(String email);
}

// JpaRepository incluye:
// - findAll() - no siempre necesario
// - save() - podría estar en otro servicio
// - delete() - podría estar separado
// - flush() - operación baja nivel
// - saveAndFlush() - podría ser innecesario
```

**Debería ser:**
```java
// ✅ SOLUCIÓN: Interfaces segregadas
public interface UsuarioReadRepository {
    Usuario findByEmail(String email);
    Optional<Usuario> findById(Long id);
    List<Usuario> findAll();
}

public interface UsuarioWriteRepository {
    Usuario save(Usuario usuario);
    void delete(Usuario usuario);
}
```

#### 4.2 JwtUtil hace demasiado

**Archivo:** `JwtUtil.java`

**Responsabilidades actuales:**
- `generateToken()` - Generación
- `validateToken()` - Validación
- `getUserIdFromToken()` - Extracción de ID
- `getEmailFromToken()` - Extracción de email
- `getRolFromToken()` - Extracción de rol
- `getUsernameFromToken()` - Legacy deprecated

**Código problemático:**
```java
// ❌ PROBLEMA: Si solo necesito generar tokens, dependo de toda la clase
// Si solo necesito validar, dependo de métodos de extracción que no uso
@Component
public class JwtUtil {
    // Todos estos métodos en una sola interfaz
    public String generateToken(...) { }
    public boolean validateToken(...) { }
    public Long getUserIdFromToken(...) { }
    public String getEmailFromToken(...) { }
    public String getRolFromToken(...) { }
}
```

**Debería ser:**
```java
// ✅ SOLUCIÓN: Interfaces segregadas
public interface TokenGenerator {
    String generateToken(Long idUsuario, String email, String rol);
}

public interface TokenValidator {
    boolean validateToken(String token);
}

public interface TokenExtractor {
    Long extractUserId(String token);
    String extractEmail(String token);
    String extractRole(String token);
}
```

### Impacto ISP

- **Acoplamiento:** Clases dependen de más métodos de los que necesitan
- **Testing:** Difícil mockear interfaces grandes

---

## 🔴 VIOLACIÓN 5: DIP - DEPENDENCY INVERSION PRINCIPLE

### Definición del Principio
**Depende de abstracciones**, no de implementaciones concretas.

### Violaciones Encontradas

#### 5.1 Controllers usan @Autowired en lugar de Constructor Injection

**Archivo:** Múltiples controllers

**Código problemático:**
```java
// ❌ PROBLEMA: Inyección de campo (field injection)
@RestController
public class CitaController {
    @Autowired private CitaRepository citaRepository;
    @Autowired private HorarioRepository horarioRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private JwtUtil jwtUtil;
}

// Problemas:
// 1. Difícil de testear (necesita reflexión)
// 2. Oculta dependencias
// 3. Las dependencias se inyectan después de construcción
// 4. Dependencias mutables
```

**Debería ser:**
```java
// ✅ SOLUCIÓN: Constructor Injection
@RestController
@RequiredArgsConstructor
public class CitaController {
    private final CitaService citaService;
    private final AuthService authService;
    
    // Fácil de testear:
    CitaController citaController = new CitaController(
        new MockCitaService(),
        new MockAuthService()
    );
}
```

#### 5.2 Controllers dependen directamente de Repositories

**Archivo:** `AuthController.java`, `CitaController.java`, etc.

**Código problemático:**
```java
// ❌ PROBLEMA: Dependencia directa de implementación
@RestController
public class AuthController {
    private final UsuarioRepository usuarioRepository;  // ← Implementación concreta
    
    @PostMapping("/login")
    public ResponseEntity<?> login(...) {
        Usuario usuario = usuarioRepository.findByEmail(email); // ← Acoplado a JPA
    }
}

// Si queremos cambiar a MongoDB o SQL nativo, debemos modificar el controller
```

**Debería ser:**
```java
// ✅ SOLUCIÓN: Depender de interfaz
public interface UsuarioDataSource {
    Optional<Usuario> obtenerPorEmail(String email);
}

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;  // ← Abstracción
    
    @PostMapping("/login")
    public ResponseEntity<?> login(...) {
        AuthResult result = authService.autenticar(credentials);
    }
}
```

#### 5.3 Controllers dependen de PasswordEncoder

**Archivo:** `AuthController.java`, `UsuarioController.java`

**Código problemático:**
```java
// ❌ PROBLEMA: Lógica de encriptación en controller
@Autowired
private PasswordEncoder passwordEncoder;

// En el método login
if (usuario != null && passwordEncoder.matches(password, usuario.getPassword())) {
    // Login exitoso
}

// ¿Qué pasa si queremos cambiar el algoritmo? Modificar todos los controllers
```

**Debería ser:**
```java
// ✅ SOLUCIÓN: Delegar a servicio
public interface CredentialValidator {
    boolean validarCredenciales(String password, String hashAlmacenado);
}

// En AuthService
boolean sonCredencialesValidas = credentialValidator.validarCredenciales(
    credentials.getPassword(),
    usuario.getPassword()
);
```

#### 5.4 SecurityConfig depende de clases concretas

**Archivo:** `SecurityConfig.java`

**Código problemático:**
```java
// ❌ PROBLEMA: Hardcoded en configuración
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(auth -> auth
        .requestMatchers("/auth/**").permitAll()
        .requestMatchers("/doctores/**").permitAll()
        // Cambios aquí requieren recompilar
    );
}
```

### Impacto DIP

- **Testabilidad:** Imposible hacer unit tests sin integración
- **Flexibilidad:** No se puede cambiar implementaciones sin modificar código
- **Mantenibilidad:** Fuerte acoplamiento entre capas

---

## 📊 MATRIZ DE VIOLACIONES POR ARCHIVO

| Archivo | SRP | OCP | LSP | ISP | DIP | Total |
|---------|-----|-----|-----|-----|-----|-------|
| AuthController.java | ❌❌ | ❌❌ | ⚠️ | - | ❌❌ | 7 |
| CitaController.java | ❌❌ | ❌❌ | ⚠️ | - | ❌ | 6 |
| UsuarioController.java | ❌ | ❌ | ⚠️ | - | ❌ | 4 |
| DoctorController.java | ❌ | ⚠️ | - | - | ❌ | 3 |
| MeController.java | - | ⚠️ | ⚠️ | - | ❌ | 2 |
| OtrosControllers | - | ⚠️ | - | - | ❌ | 2 |
| JwtUtil.java | ⚠️ | ⚠️ | - | ❌ | - | 2 |
| SecurityConfig.java | - | ❌ | - | - | ⚠️ | 2 |

---

## 🎯 IMPACTO CRÍTICO EN PRODUCCIÓN

### Problemas que causan estos principios violados:

1. **Testing Unit imposible**
   - No hay forma de testear la lógica sin un servidor HTTP
   - No se pueden crear mocks fácilmente
   
2. **Deuda técnica exponencial**
   - Cada nuevo endpoint replica código existente
   - Cambios se propagaban a 5+ archivos
   
3. **Bugs difíciles de encontrar**
   - Lógica dispersada = lógica duplicada = inconsistencias
   - El cambio de contraseña se valida diferente en 2 lugares
   
4. **Escalabilidad limitada**
   - No se puede agregar nuevos tipos de usuario fácilmente
   - No se puede cambiar BD sin refactorizar controllers

5. **Documentación vs Código**
   - La única "documentación" está en el código
   - Los cambios no se propagan a toda la codebase

---

## ✅ PRÓXIMOS PASOS

Ver: `ARQUITECTURA_REFACTORIZADA_SOLID.md` para la solución completa.
