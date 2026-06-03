# Reporte de Calidad — MS_Finanzas
**SonarCloud · JaCoCo · Fecha: 2026-06-02**

---

## 1. Security Hotspot — CORREGIDO

### Endpoints de Actuator expuestos sin autenticación

| Campo | Detalle |
|---|---|
| **Severidad** | Alta |
| **Regla SonarCloud** | `java:S4502` |
| **Clase** | `SecurityConfig` |
| **Archivo** | `src/main/java/com/example/demo/infra/config/SecurityConfig.java` |
| **Línea original** | 41 |
| **Commit** | `2956beb` |

**Problema:** Spring Boot Actuator expone endpoints sin autenticación. Un atacante podía acceder a `/actuator/env` (variables de entorno con contraseñas/tokens) y `/actuator/heapdump` (volcado de memoria del proceso).

```java
// ANTES — línea 41 de SecurityConfig.java
.requestMatchers(
    "/swagger-ui/**",
    "/swagger-ui.html",
    "/v3/api-docs/**",
    "/actuator/**"          // expone TODO Actuator sin autenticación
).permitAll()
```

```java
// DESPUÉS
.requestMatchers(
    "/swagger-ui/**",
    "/swagger-ui.html",
    "/v3/api-docs/**",
    "/actuator/health"      // solo el health check es público
).permitAll()
```

---

## 2. Code Smells — CORREGIDOS

### 2.1 String literal duplicado 3 veces

| Campo | Detalle |
|---|---|
| **Severidad** | Media |
| **Regla SonarCloud** | `java:S1192` |
| **Clase** | `SavingGoalService` |
| **Archivo** | `src/main/java/com/example/demo/application/service/SavingGoalService.java` |
| **Líneas originales** | 71, 99 y 107 |
| **Commit** | `12d415a` |

**Problema:** El texto `"Meta de ahorro no encontrada con ID: "` aparecía literalmente en 3 métodos distintos del mismo archivo.

```java
// ANTES — líneas 71, 99 y 107
.orElseThrow(() -> new SavingGoalNotFoundException(
    "Meta de ahorro no encontrada con ID: " + goalId));  // repetido x3
```

```java
// DESPUÉS — constante definida en línea 24
private static final String META_NO_ENCONTRADA = "Meta de ahorro no encontrada con ID: ";

.orElseThrow(() -> new SavingGoalNotFoundException(META_NO_ENCONTRADA + goalId));
```

---

### 2.2 Método sin implementar que lanza excepción en producción

| Campo | Detalle |
|---|---|
| **Severidad** | Alta |
| **Regla SonarCloud** | `java:S1130` |
| **Clase** | `TransactionService` |
| **Archivo** | `src/main/java/com/example/demo/application/service/TransactionService.java` |
| **Método** | `findFiltered()` |
| **Líneas originales** | 135–136 |
| **Commit** | `12d415a` |

**Problema:** El método existía en la interfaz pero su implementación lanzaba `UnsupportedOperationException`. Cualquier llamada desde el controlador provocaba un error 500 en producción.

```java
// ANTES — líneas 135-136
public List<Transaction> findFiltered(...) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findFiltered'");
}
```

```java
// DESPUÉS
public List<Transaction> findFiltered(TypeTransaction tipo, UUID categoriaId,
        UUID titularId, LocalDate desde, LocalDate hasta) {
    TransactionListFilter filter = new TransactionListFilter(
        Optional.ofNullable(tipo),
        Optional.ofNullable(categoriaId),
        Optional.empty(),
        Optional.ofNullable(titularId)
    );
    return transactionRepositoryPort.findAll(filter);
}
```

---

### 2.3 Collector obsoleto

| Campo | Detalle |
|---|---|
| **Severidad** | Baja |
| **Regla SonarCloud** | `java:S6204` |
| **Clase** | `TransactionController` |
| **Archivo** | `src/main/java/com/example/demo/infra/rest/TransactionController.java` |
| **Línea original** | 64 |
| **Commit** | `12d415a` |

**Problema:** `collect(Collectors.toList())` es la forma antigua (Java 8). Desde Java 16 existe `.toList()`, más conciso y que produce una lista inmutable.

```java
// ANTES — línea 64
transactions.stream()
    .map(transactionResponseMapper::toResponse)
    .collect(Collectors.toList())
```

```java
// DESPUÉS
transactions.stream()
    .map(transactionResponseMapper::toResponse)
    .toList()
```

---

### 2.4 `equals()` no hereda correctamente en clase con herencia

| Campo | Detalle |
|---|---|
| **Severidad** | Media |
| **Regla SonarCloud** | `java:S2160` |
| **Clase** | `SavingGoalResponse` |
| **Archivo** | `src/main/java/com/example/demo/infra/rest/dto/SavingGoalResponse.java` |
| **Línea original** | 14 |
| **Commit** | `12d415a` |

**Problema:** `SavingGoalResponse` extiende `RepresentationModel` (HATEOAS). Sin `callSuper = true`, Lombok genera un `equals()` que ignora los campos del padre (los links de HATEOAS), produciendo comparaciones incorrectas entre objetos.

```java
// ANTES — línea 14
@EqualsAndHashCode
public class SavingGoalResponse extends RepresentationModel<SavingGoalResponse> {
```

```java
// DESPUÉS
@EqualsAndHashCode(callSuper = true)
public class SavingGoalResponse extends RepresentationModel<SavingGoalResponse> {
```

---

### 2.5 Lambda con múltiples puntos de fallo en `assertThrows`

| Campo | Detalle |
|---|---|
| **Severidad** | Media |
| **Regla SonarCloud** | `java:S5778` |
| **Clase** | `SavingGoalValidatorTest` |
| **Archivo** | `src/test/java/com/example/demo/domain/validation/SavingGoalValidatorTest.java` |
| **Líneas originales** | 56 y 61 |
| **Commit** | `fb26866` |

**Problema:** El lambda de `assertThrows` contenía `LocalDate.now().minusDays(1)` — dos llamadas adicionales que también pueden lanzar excepción — junto al método bajo prueba. No se puede garantizar cuál de las tres invocaciones lanzó la excepción.

```java
// ANTES — línea 56
assertThrows(IllegalArgumentException.class,
    () -> SavingGoalValidator.validateFechaLimite(LocalDate.now().minusDays(1)));
//   ↑ 3 posibles puntos de fallo dentro del lambda

// ANTES — línea 61
assertThrows(IllegalArgumentException.class,
    () -> SavingGoalValidator.validateFechaLimite(LocalDate.now()));
```

```java
// DESPUÉS
LocalDate pastDate = LocalDate.now().minusDays(1);  // calculado fuera del lambda
assertThrows(IllegalArgumentException.class,
    () -> SavingGoalValidator.validateFechaLimite(pastDate));
//   ↑ único punto de fallo dentro del lambda

LocalDate today = LocalDate.now();
assertThrows(IllegalArgumentException.class,
    () -> SavingGoalValidator.validateFechaLimite(today));
```

---

## 3. Cobertura de tests

### Resultado final

| Métrica | Antes | Después | Umbral requerido |
|---|---|---|---|
| Instrucciones | 64.4% | **98.4%** | >= 80% |
| Ramas | 49.0% | **90.7%** | >= 80% |
| Líneas | 55.2% | **99.1%** | >= 80% |
| Métodos | 81.8% | **96.4%** | >= 80% |
| Clases | 97.5% | **100%** | >= 80% |

**Total de tests:** 190 → **251** (+61 tests nuevos)

### Causa del bajo porcentaje inicial

Las 18 implementaciones generadas por MapStruct (`*MapperImpl`) nunca eran instanciadas directamente en los tests. El paquete `infra.mapper` tenía solo **9.6%** de cobertura a pesar de ser usado en producción.

**Solución:** Se creó `MapperImplTest.java` que instancia cada implementación con `new XxxMapperImpl()` y valida el caso `null` (retorna `null`) y el caso con datos reales. El paquete pasó a **99.7%**.

### Tests nuevos añadidos por archivo

| Archivo | Tests añadidos | Casos cubiertos |
|---|---|---|
| `CategoryServiceTest.java` | +7 | `findById`, `findAll`, `updateCategory` éxito y nombre duplicado, `deleteCategoryById` not found |
| `TransactionServiceTest.java` | +8 | `updateTransaction` éxito, `findAll`, `findById`, `deleteTransaction`, `findFiltered`, presupuesto excedido/no excedido, categoría vacía ya existente |
| `SavingGoalServiceTest.java` | +7 | `addSavingGoal`, `findById`, `findAll`, duplicado en update, mismo nombre, `deleteSavingGoalById` not found, `markAsCompleted` not found |
| `TitularServiceTest.java` | +4 | `findById`, `updateTitular` not found, token generado vs existente |
| `SavingGoalValidatorTest.java` | +9 | `validateMonto` null/negativo, `validateNombre` todos los paths, `validateFechaLimite` todos los paths, `validateTitular` todos los paths |
| `DateRangeValidatorTest.java` *(nuevo)* | +4 | Null, fecha igual, fecha posterior, fecha anterior |
| `MapperImplTest.java` *(nuevo)* | +51 | 18 implementaciones MapStruct: null input, datos completos, relaciones null |

---

## 4. Complejidad ciclomática por clase

> **Umbral requerido: < 50 por clase — Todas las clases lo cumplen.**
> El valor 377 es la suma total del proyecto, no un umbral individual.

| Clase | Archivo | Complejidad |
|---|---|---|
| `JpaTransactionRepositoryAdapter` | `infra/persistence/repository/` | 23 |
| `TransactionService` | `application/service/` | 19 |
| `SavingGoalService` | `application/service/` | 14 |
| `BudgetService` | `application/service/` | 12 |
| `SavingGoalValidator` | `domain/validation/` | 12 |
| `CategoryService` | `application/service/` | 11 |
| `JpaSavingGoalRepositoryAdapter` | `infra/persistence/repository/` | 11 |
| `ReportEntityMapperImpl` | `infra/mapper/` | 11 |
| `SavingGoalController` | `infra/rest/` | 10 |
| `TransactionRequestMapper` | `infra/mapper/` | 10 |
| `JpaCategoryRepositoryAdapter` | `infra/persistence/repository/` | 9 |
| `TransactionController` | `infra/rest/` | 9 |
| `GlobalExceptionHandler` | `domain/exception/` | 9 |
| `TitularService` | `application/service/` | 9 |
| `ReportService` | `application/service/` | 9 |
| `JpaBudgetRepositoryAdapter` | `infra/persistence/repository/` | 8 |
| `JwtUtil` | `infra/security/` | 8 |
| `CategoryController` | `infra/rest/` | 8 |
| `BudgetController` | `infra/rest/` | 8 |
| Resto de clases (60 clases) | — | <= 7 |

---

## 5. Recomendaciones para el equipo

1. **No exponer `/actuator/**` completo.** Si se necesita un nuevo endpoint de Actuator, evaluar explícitamente si debe ser público o autenticado.
2. **Extraer constantes para mensajes de error repetidos.** Aplica en cualquier servicio donde el mismo texto aparezca en múltiples `orElseThrow` o `throw new`.
3. **No dejar `UnsupportedOperationException` ni `TODO` en métodos de interfaces implementadas.** Detectarlos en revisión de PR antes de mergear a main.
4. **Al usar `assertThrows` en tests,** calcular los argumentos fuera del lambda para que solo la invocación bajo prueba esté dentro.
5. **Al usar `@EqualsAndHashCode` de Lombok en subclases,** siempre agregar `callSuper = true`.
6. **Usar `.toList()` en lugar de `collect(Collectors.toList())`** en proyectos con Java 16+.
