## INTERNAL NOTES (FOR DEVELOPER ONLY - AI IGNORE)
- Ensure to call this rules from GEMINI.md (file automatically reader by gemini)
## END INTERNAL NOTES FOR DEVELOPER




## Profile: Senior Android Engineer

# Android Project Guidelines: Multi-Module Feature-by-Layer Architecture, Clean Architecture, SOLID & MVI

This document serves as a reference for AI agents and developers working on this Android project. It establishes the architectural standards, design patterns, and best practices to ensure code quality, maintainability, and scalability.

## 1. Architecture Overview: Multi-Module Feature-by-Layer Architecture

The project follows a modern **Feature-by-Layer Multi-Module Architecture** to maximize compilation speed, reusability, testability, and strict decoupling. The codebase is organized into multi-project Gradle modules divided by layers and feature domains:

```
                          ┌──────────┐
                          │   :app   │
                          └────┬─────┘
                               │
            ┌──────────────────┼──────────────────┐
            ▼                  ▼                  ▼
       ┌──────────┐      ┌───────────┐       ┌──────────┐
       │   :di    │      │  :feature │       │  :data   │
       └────┬─────┘      └─────┬─────┘       └────┬─────┘
            │                  │                  │
            └──────────┬───────┴───────┬──────────┘
                       ▼               ▼
                  ┌─────────┐     ┌──────────┐
                  │  :core  │ ──> │ :domain  │ (Pure Kotlin 100%)
                  └─────────┘     └──────────┘
```

### 🟢 Domain Module (`:domain`)
*   **Responsibility:** Pure Kotlin business logic, domain models, repository interfaces, and use cases.
*   **Dependencies:** **None (0 dependencies)**. Must remain 100% Pure Kotlin, free from Android framework and third-party UI libraries.
*   **Components:**
    *   **Domain Models:** Pure data classes representing business objects (e.g., `Character`, `ThemeMode`).
    *   **Use Cases (Interactors):** Encapsulate specific business logic (e.g., `SearchCharactersUseCase`, `GetCharacterDetailUseCase`). Each Use Case should have a single public `invoke` function.
    *   **Repository Interfaces:** Abstractions defining data operations (e.g., `CharacterRepository`).

### 🔵 Core Module (`:core`)
*   **Responsibility:** Design system composables, app theme, shared UI models, base composables, and pure UI helper utilities.
*   **Dependencies:** `:domain`.
*   **Components:**
    *   **Design System:** `RickAndMortyTheme`, color tokens, typography, shapes.
    *   **Reusable Composables:** Button, Text, ProgressButton, AsyncImage, etc.
    *   **UI Models:** Immutable UI view models and UI extension mappers.
    *   **Utilities:** `DispatcherProvider`, extension functions (`Int.empty()`, `String.empty()`, etc.).

### 🟡 Data Module (`:data`)
*   **Responsibility:** Data retrieval, local persistence, and remote API networking.
*   **Dependencies:** `:domain`.
*   **Components:**
    *   **Repository Implementations:** Implement domain repository interfaces, orchestrating flow between local and remote sources.
    *   **Data Sources:**
        *   *Remote:* Retrofit HTTP client (`RickAndMortyApi`).
        *   *Local:* In-memory cache with TTL (`CharacterMemoryCache`).
    *   **Mappers:** Functions to transform Data entities/DTOs into Domain models and vice-versa. API DTOs never escape the data layer.

### 🟣 Feature Modules (`:feature:*`)
*   **Responsibility:** Rendering UI, handling user interactions, and managing feature-specific state.
*   **Modules:** `:feature:characters` (includes `SearchScreen`, `DetailScreen`).
*   **Dependencies:** `:core`, `:domain`. (No direct compile-time dependencies between feature modules).
*   **Components:**
    *   **UI Roots & Screens:** `SearchScreen`, `DetailScreen`.
    *   **ViewModels:** Manage UI state and execute domain Use Cases.
    *   **MVI Contracts:** Define State, Events (Intents), and Side-Effects.

### ⚙️ Dependency Injection Module (`:di`)
*   **Responsibility:** Centralized Koin module declarations (`coreModule`, `dataModule`, `domainModule`, `charactersFeatureModule`).
*   **Dependencies:** `:data`, `:domain`, `:core`, and all `:feature:*` modules.

### 📱 Application Module (`:app`)
*   **Responsibility:** Application entry point (`RickAndMortyApplication`), `MainActivity`, Navigation Graph (`CharactersNavigation`, `NavDestination`), and app-level ViewModels (`MainViewModel`).
*   **Dependencies:** `:di`, `:core`, `:domain`, `:data`, and all `:feature:*` modules.

---

## 2. Design Pattern: MVI (Model-View-Intent)

We use **MVI** for state management to ensure a unidirectional data flow and predictable UI states.

### Key Components
1.  **State:** An **immutable** data class representing the single source of truth for a specific screen/view.
    *   *Example:* `data class SearchUiState(val query: String, val isLoading: Boolean, val characters: List<Character>, val error: String?)`
2.  **Event (Intent):** Represents user actions or system events that trigger logic.
    *   *Example:* `sealed interface SearchUiIntent { data class QueryChanged(val query: String) : SearchUiIntent; data class CharacterClicked(val id: Int) : SearchUiIntent }`
3.  **Effect (Side Effect):** One-time events that shouldn't persist in the state (e.g., Navigation, Snackbars, Toasts).
    *   *Example:* `sealed interface SearchUiEffect { data class NavigateToDetail(val characterId: Int) : SearchUiEffect; data class ShowError(val message: String) : SearchUiEffect }`

### Flow
1.  **View** emits an **Event**.
2.  **ViewModel** processes the Event, executes Use Cases (Domain).
3.  **ViewModel** updates the **State** (via `StateFlow`) or emits an **Effect** (via `SharedFlow`/`Channel`).
4.  **View** observes **State** to render UI and collects **Effects** for navigation/alerts.

### Model
1. Use a sealed interface for domain/repository execution results where appropriate:
```kotlin
sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: Throwable) : Result<Nothing>
}
```

---

## 3. SOLID Principles in Context

*   **S - Single Responsibility Principle:**
    *   *Apply:* Each Use Case does exactly one thing. ViewModels handle state logic, not UI rendering or data fetching.
*   **O - Open/Closed Principle:**
    *   *Apply:* Use interfaces for Repositories and Data Sources to allow swapping implementations (e.g., Mock vs Real) without changing client code.
*   **L - Liskov Substitution Principle:**
    *   *Apply:* Implementations of interfaces should behave predictably so they can be interchanged without breaking the app.
*   **I - Interface Segregation Principle:**
    *   *Apply:* Create specific interfaces if a client doesn't need all methods.
*   **D - Dependency Inversion Principle:**
    *   *Apply:* The Domain layer defines interfaces; the Data layer implements them. Use Dependency Injection (Koin) to provide implementations.

---

## 4. Best Practices

### Kotlin & Coroutines
*   Use `suspend` functions for asynchronous operations.
*   Use `Flow` for reactive data streams (especially from Data to Domain to UI).
*   Always use appropriate `Dispatcher` (IO for DB/Network, Main for UI).

### UI (Jetpack Compose)
*   Composables should be stateless whenever possible (hoist state to parent or ViewModel).
*   Use `@Preview` functions to test UI components in isolation.
*   Follow Material 3 Design guidelines.
*   In the View (Compose), always use `collectAsStateWithLifecycle()` to observe State and avoid memory leaks.
*   Each Composable Panel must have a 'Content' version containing only the State and Events (lambdas), to facilitate the creation of Previews without the need to mock the ViewModel. The 'Content' composable is called by a 'Root' composable which obtains its ViewModel state.
*   Always use colors from `com.davcres.rickandmorty.core.ui.theme.RickAndMortyTheme` and follow core design system composables style.
*   In Text composables use `style = MaterialTheme.typography` instead of hardcoded `fontSize`.

### Testing
*   **Dependencies:** Use MockK and JUnit5 for unit tests. Use Jetpack Compose Tests with JUnit 4 for UI tests.
*   **Domain:** 100% unit test coverage for Use Cases (fast, pure Kotlin).
*   **Data:** Unit test Repositories and Mappers using MockK.
*   **Presentation / Features:** Unit test ViewModels (verify State updates and Effects).
*   Every time a new class is created or modified, ensure existing tests pass and write new tests as needed.
*   Share the same `TestDispatcher` instance across tests using `MainDispatcherExtension`.

### Naming Conventions
*   **Use Cases:** `Verb` + `Noun` + `UseCase` (e.g., `SearchCharactersUseCase`).
*   **Repositories:** `DataName` + `Repository` (e.g., `CharacterRepository`).
*   **Implementations:** `Name` + `Impl` (e.g., `CharacterRepositoryImpl`).

---

## 5. Project Organization: Multi-Module Layout

```
RickAndMorty
├── app               (Application, MainActivity, NavHost, RickAndMortyApplication)
├── core              (Theme, DesignSystem composables, DispatcherProvider, extensions)
├── domain            (100% Pure Kotlin: Use Cases, Repositories, Domain Models)
├── data              (Retrofit remote API, Memory Cache with TTL, Repository Impls, dataModule)
├── di                (Central Koin modules aggregator: appModules)
└── feature           (Independent UI Feature Modules)
    └── characters    (SearchScreen, DetailScreen, SearchViewModel, DetailViewModel, charactersFeatureModule)
```
