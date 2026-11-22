# Business & Functional Rules: Rick and Morty App

This document defines the functional scope, product goals, technical rules, and constraints for the Rick and Morty Android application. All architectural, implementation, and testing decisions must align with the guidelines laid out here.

---

## 1. Objective

Build a simple, robust Android application that consumes the public **Rick and Morty API** and demonstrates senior-level best practices in:
- **Architecture & Clean Separation of Concerns**
- **Concurrency & Coroutine Cancellation**
- **In-Memory Caching & TTL Management**
- **Testing (Domain, Data, Presentation)**

---

## 2. Functional Requirements

### 2.1 Search Screen
- **Initial View:** The app must present an initial screen containing a search text field to query characters/items against the Rick and Morty API (https://rickandmortyapi.com/documentation).
- **Results Presentation:** Query results must be displayed cleanly in a list/grid format with relevant item summaries (e.g., character avatar image, name, status, species).
- **Pagination (Infinite Scroll):** Character queries are paginated by the API (20 items per page). The search screen supports infinite scrolling, automatically fetching and appending subsequent pages as the user scrolls, while displaying loading and retry indicators for pagination failures without discarding loaded items.
- **Empty / Loading / Error States:** The UI must handle and display distinct states: initial empty state, loading indicator during active network/cache retrieval, empty results message if no matches exist, and error feedback when network failures occur.

### 2.2 Cancellation & Request Handling
- **Active Cancellation:** If a user performs a new search (e.g., typing a new query or modifying input) while a previous query is in flight:
  - The ongoing network/asynchronous task must be immediately canceled.
  - Stale results from previous queries must never be displayed or overwrite newer queries.
  - Proper coroutine cancellation (`Job.cancel()` or Flow operators like `restartable` / `flatMapLatest` / debounce) must be used.

### 2.3 Detail Screen
- **Navigation:** Selecting any item from the search results list must navigate to a dedicated detail screen.
- **Content:** The detail screen must display comprehensive information about the selected character, including at least **three or more distinct attributes** (e.g., Name, Status & Species, Origin, Last Known Location, Gender, Image).

---

## 3. Technical Requirements

### 3.1 Architecture
- **Layer Separation:** Strict separation into **Data**, **Domain**, and **Presentation** layers or modules (following Clean Architecture and MVI as specified in `android-rules.md`).
- **Domain Independence:** The Domain layer must be 100% pure Kotlin with zero Android dependencies, exposing repository interfaces and single-responsibility use cases.
- **Unidirectional Data Flow (MVI):** Presentation must follow MVI pattern with immutable UI States (`UiState`), UI Events/Intents (`UiIntent`), and one-off Side Effects (`UiEffect`).

### 3.2 UI Framework
- All user interfaces must be built natively using **Jetpack Compose**.
- Use design system tokens, Material 3 components, and theme colors defined in the design guidelines.

### 3.3 Concurrency & Asynchrony
- Implemented with **Kotlin Coroutines** and **Flow**.
- The search pipeline must properly handle debouncing and cooperative cancellation so background operations do not leak or run unnecessarily.

### 3.4 Caching Strategy
- **Per-Query & Per-Page Caching:** Search query results must be cached per query string and page index (`query#page`).
- **Time-to-Live (TTL):** Cached results must have a limited validity window (e.g., **2 to 5 minutes**).
- **Cache Hit / Invalidation Logic:**
  - If a valid (non-expired) cache entry exists for the requested query and page, return it immediately without initiating a network request.
  - If no cache entry exists or the TTL has expired, fetch fresh data from the network, update the cache with a new timestamp, and return the data.
- **Storage Scope:** An **in-memory cache** is sufficient and required (disk persistence via Room/DataStore is optional).

### 3.5 Testing Strategy
- **Domain Layer Tests:** Unit test use cases using mocked repositories (e.g., verifying search execution and business mappings).
- **Data Layer Tests:** Validate repository and data source cache behavior:
  - Cache hit returns cached data without hitting the remote API.
  - Cache miss triggers network request.
  - Cache expiration (after TTL) triggers a fresh network call and refreshes the cache.
- **Presentation Layer Tests (Optional but valued):** Test ViewModel state transitions, search debouncing, and cancellation behavior.

---

## 4. Deliverables

1. **Source Code & Tests:** Complete Android project adhering to the quality gates in `github-rules.md` and architecture rules in `android-rules.md`.
2. **README:** A concise documentation file detailing:
   - The chosen public API (endpoints used).
   - Instructions on how to build and run the app.
   - Core technical decisions (architecture, concurrency/cancellation strategy, caching mechanism and TTL).

---

## 5. Notes & Core Values

- **Simplicity Over Over-Engineering:** The application scope is deliberately focused. Overly complex design patterns or excessive extra features are not required.
- **Key Focus Areas:** 
  - Code clarity and readability.
  - Robust separation of concerns.
  - Correct and leak-free concurrency and cancellation handling.
  - Deterministic and well-tested caching logic.

