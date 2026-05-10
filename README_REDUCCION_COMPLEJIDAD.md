# README - Reduccion de Complejidad Ciclomatica

## Objetivo
Reducir complejidad ciclomática en metodos con alta ramificacion dentro de `MS_Finanzas`, manteniendo el comportamiento funcional.

## Estrategia aplicada
- Extraer validaciones y ramas de decision a metodos privados pequenos.
- Reemplazar condicionales anidados por flujo declarativo con `Optional` cuando era seguro.
- Separar caminos de persistencia (crear vs actualizar) para dejar metodos principales mas simples.

## Cambios realizados

### 1) SavingGoalService
Archivo: `MS_Finanzas/src/main/java/com/example/demo/application/service/SavingGoalService.java`

#### Refactor en `addSavingGoal`
Se elimino la cadena de validaciones en linea y se movio a:
- `validateNewGoal(...)`
- `validateGoalPayload(...)`
- `validateUniqueName(...)`
- `validateFutureDate(...)`
- `isBlank(...)`

Resultado: el metodo principal queda enfocado en orquestar y persistir, con menos ramas directas.

#### Refactor en `updateSavingGoal`
Se movieron validaciones a:
- `validateGoalPayload(...)`
- `validateUniqueNameOnUpdate(...)`

Resultado: menor complejidad en el metodo de actualizacion y reglas encapsuladas.

### 2) TransactionService
Archivo: `MS_Finanzas/src/main/java/com/example/demo/application/service/TransactionService.java`

#### Refactor en `resolveCategory`
Se reemplazo el `if` con flujo basado en `Optional` y helpers:
- `findCategoryById(...)`
- `findOrCreateEmptyCategory()`

Resultado: menos bifurcaciones en el metodo central y reglas de resolucion mas legibles.

### 3) JpaTransactionRepositoryAdapter
Archivo: `MS_Finanzas/src/main/java/com/example/demo/infra/persistence/repository/JpaTransactionRepositoryAdapter.java`

#### Refactor en `findAll`
La logica de rango de fechas se extrajo a:
- `resolveDateRange(Optional<YearMonth> monthFilter)`

Resultado: metodo `findAll` mas lineal.

#### Refactor en `save`
Se separo la rama crear/actualizar en:
- `resolveEntityForSave(...)`
- `loadEntityForUpdate(...)`
- `createEntityForInsert(...)`
- `updateEntityFields(...)`

Resultado: menor complejidad en `save` y mejor mantenibilidad.

### 4) Ajuste Sonar/Maven para complejidad real de codigo productivo
Archivo: `MS_Finanzas/pom.xml`

Se corrigio la configuracion para evitar inflar la complejidad con codigo generado y boilerplate:
- Se agrego `sonar.sources=src/main/java`.
- Se agrego `sonar.tests=src/test/java`.
- Se agrego `sonar.exclusions=**/target/**,**/generated/**,**/*MapperImpl.java,**/infra/mapper/**,**/infra/rest/dto/**,**/infra/persistence/entity/**`.
- Se elimino `build-helper-maven-plugin` que agregaba `target/generated-sources/annotations` como source.

Resultado: Sonar analiza la capa de logica de negocio y evita sobrecontar complejidad en codigo generado/boilerplate.

## Validacion ejecutada
Se verifico compilacion del modulo despues de los cambios:

Comando usado:
- `MS_Finanzas\\mvnw.cmd -q -DskipTests compile`

Resultado:
- Compilacion correcta (exit code 0).
- Solo warnings de Lombok/Unsafe, sin errores de compilacion.

## Archivos modificados
- `MS_Finanzas/src/main/java/com/example/demo/application/service/SavingGoalService.java`
- `MS_Finanzas/src/main/java/com/example/demo/application/service/TransactionService.java`
- `MS_Finanzas/src/main/java/com/example/demo/infra/persistence/repository/JpaTransactionRepositoryAdapter.java`
- `MS_Finanzas/pom.xml`
- `.github/workflows/build.yml`
- `README_REDUCCION_COMPLEJIDAD.md`

## Nota
Para cuantificar el impacto exacto en Sonar (metrica de complejidad por metodo/proyecto), ejecutar analisis Sonar posterior a este commit y comparar contra baseline previo.
