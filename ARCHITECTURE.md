# Rent Truth BD — Architecture Map 🏗️

This document describes the architectural patterns, state managers, and data synchronization flows designed inside the Rent Truth BD ecosystem.

---

## 🗂️ Architectural Boundaries (MVVM Pattern)

Rent Truth BD strictly adheres to clean separation of concerns using the Model-View-ViewModel (MVVM) architecture optimized with Kotlin Coroutines and StateFlow.

```
       +-------------------------------------------------------+
       |                     UI View Layer                     |
       |                   (Jetpack Compose)                   |
       +-------------------------------------------------------+
                                  |
         Collects reactive states | Reads user triggers
                                  v
       +-------------------------------------------------------+
       |                    ViewModel Layer                    |
       |                   (RentViewModel)                     |
       +-------------------------------------------------------+
                                  |
          Triggers DAO operations | Exposes database flows
                                  v
       +-------------------------------------------------------+
       |                   Repository Layer                    |
       |                   (RentRepository)                    |
       +-------------------------------------------------------+
                                  |
              Queries & Inserts   | Flow streams
                                  v
       +----------------------------+--------------------------+
       |   In-Memory Registry       |   Local Database (Room)  |
       |   (Users, JWT Sessions)    |   (Listings, Payments)   |
       +----------------------------+--------------------------+
```

### 1. View Layer (Composables)
- Handles visual rendering using Material 3 UI design grids.
- Listens to atomic state emissions via `.collectAsState()` extension hooks.
- Forwards user actions directly to the `RentViewModel` without holding business logic or mutable state registers.

### 2. ViewModel Layer (`RentViewModel`)
- Orchestrates UI-facing event handlers and holds the primary thread-safe StateFlow elements.
- Exposes immutable StateFlow pipelines (`listings`, `negotiations`, `manualPayments`, etc.) using `stateIn` with `SharingStarted.WhileSubscribed(5000)` filters.
- Resolves Gemini AI responses and simulates background operations asynchronously.

### 3. Repository Layer (`RentRepository`)
- Direct intermediary representing single sources of truth.
- Coordinates calls between Room SQLite local engines and mock REST/Ktor frameworks.

---

## 🔄 Real-time Synchronization Loop (`RentSync`)

To secure reliable landlord-tenant registrations across unstable mobile networks in Bangladesh, a strict websocket synchronization simulator is implemented inside the client scope.

### 1. Synchronization Flow Mechanics
```
[User Form Action]
       │
       ▼ (Optimistic UI state update)
[View State Changed] ───► Logs Queue Backup
       │
       ▼ (Database Transaction Safe Write)
[DAO SQLite Insert/Update]
       │
       ├───► [Sync Success] ➔ Purges queue backup disk files 
       │
       └───► [Internet Loss] ➔ Automatically schedules retro-retries (Backoff)
```

### 2. Guarding Against Race Conditions
- **Double Claims Protection**: Unique constraints at database levels block dual submissions of matching Transaction IDs.
- **Lock-state Synchronization**: Landlord, tenant, and broker profiles feature reactive verification lock guards. If an account is suspended, all corresponding listing interactions block instantly.
- **Rollback Controllers**: If any financial validation fails inside the local SQL transaction block, states rollback automatically to target balances to avoid phantom money updates.
