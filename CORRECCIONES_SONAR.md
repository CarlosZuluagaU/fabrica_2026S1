# Reporte de Correcciones de Mantenibilidad - SonarQube

En este documento se detallan las correcciones aplicadas para resolver los problemas de mantenibilidad reportados por SonarQube.

---

### 1. `TransactionController.java`

**Problema:** Uso de `Stream.collect(Collectors.toList())` en lugar del método más moderno `Stream.toList()`.

**Antes:**
```java
// ...
import java.util.stream.Collectors;
// ...

@GetMapping
public ResponseEntity<List<TransactionResponse>> list(
    // ...
) {
    // ...
    List<Transaction> transactions = transactionService.findAll(filter);
    return ResponseEntity.ok(
        transactions.stream().map(transactionResponseMapper::toResponse).collect(Collectors.toList())
    );
}
// ...
```

**Después:**
```java
// ...
// Se elimina la importación de java.util.stream.Collectors si no se usa en otro lugar.
// ...

@GetMapping
public ResponseEntity<List<TransactionResponse>> list(
    // ...
) {
    // ...
    List<Transaction> transactions = transactionService.findAll(filter);
    return ResponseEntity.ok(
        transactions.stream().map(transactionResponseMapper::toResponse).toList()
    );
}
// ...
```

---

### 2. `SavingGoalRequest.java`

**Problema:** Uso del atributo `required` en la anotación `@Schema`, el cual está obsoleto.

**Antes:**
```java
public record SavingGoalRequest(

    @Schema(description = "Nombre de la meta de ahorro", example = "Viaje a Cuba", required = true)
    String nombre,

    @Schema(description = "Monto objetivo en pesos colombianos", example = "12000000", required = true)
    Double montoObjetivo,

    // ...

    @Schema(
        description = "ID del titular financiero (debe existir en la base de datos)",
        example = "aa8a8b1d-e583-4168-97f6-64e6a6986397",
        required = true
    )
    UUID titularId

) {}
```

**Después:**
```java
public record SavingGoalRequest(

    @Schema(description = "Nombre de la meta de ahorro", example = "Viaje a Cuba")
    String nombre,

    @Schema(description = "Monto objetivo en pesos colombianos", example = "12000000")
    Double montoObjetivo,

    // ...

    @Schema(
        description = "ID del titular financiero (debe existir en la base de datos)",
        example = "aa8a8b1d-e583-4168-97f6-64e6a6986397"
    )
    UUID titularId

) {}
```
*Nota: La validación de campos requeridos ahora se maneja con anotaciones de `jakarta.validation` como `@NotNull` o `@NotBlank` directamente en el record, lo que es una práctica más estándar.*

---

### 3. `TitularServiceTest.java`

**Problema:** Presencia de importaciones no utilizadas y duplicadas.

**Antes:**
```java
// ...
import java.time.Instant;
import java.util.List; // No utilizada
import java.util.Optional;
import java.util.UUID; // Duplicada

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any; // No utilizada
import static org.mockito.ArgumentMatchers.eq; // No utilizada
import static org.mockito.Mockito.*;
import java.util.UUID; // Duplicada

import static org.assertj.core.api.Assertions.assertThat; // Redundante con el import estático de Assertions.*
import static org.mockito.Mockito.when; // Redundante con el import estático de Mockito.*
// ...
```

**Después:**
```java
// ...
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
// ...
```
---
