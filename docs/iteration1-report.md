# SmartCart — Iteration 1 Report

## Project Links

- **Git Repository:** https://github.com/KadenN-lab/Group4CMPT276
- **Web App (Render):** *(to be deployed)*
- **Screencast:** *(link to screencast)*

---

## Overview

SmartCart is an intelligent meal planning and grocery integration platform. In Iteration 1 we established the full-stack foundation: a Spring Boot backend with PostgreSQL and Flyway migrations, session-based authentication with registration and login, a Gemini AI-powered meal plan generator, and a responsive HTML/CSS/JavaScript frontend with a multi-step onboarding wizard. The system supports user registration and login, generating personalized weekly meal plans, viewing recipes, building aggregated grocery lists, and managing dietary preferences and pantry items. All API endpoints are gated behind authentication, returning 401 for unauthenticated requests. New users complete onboarding before signing up, with preferences saved only after account creation.

---

## User Stories

### Epic 1: Authentication and Role-Based Views

#### Story 1.1 — User Registration ✅

> **As a** new visitor, **I want to** create an account with my name, email, and password **so that** my meal plans and preferences are saved to my profile.

| # | Test Case | Expected Result | Status |
|---|-----------|-----------------|--------|
| 1 | Submit signup form with valid name, email, and password | Account is created, redirected to login with success message | ✅ Pass |
| 2 | Submit signup with an email that already exists | Error message: "An account with this email already exists" | ✅ Pass |
| 3 | Submit signup with mismatched password confirmation | Error message: "Passwords do not match" | ✅ Pass |
| 4 | Submit signup with empty required fields | Error message: "All fields are required" | ✅ Pass |
| 5 | Submit signup with invalid email (no @) | Error message: "Please enter a valid email address" | ✅ Pass |

#### Story 1.2 — User Login ✅

> **As a** registered user, **I want to** log in with my email and password **so that** I can access my saved meal plans and preferences.

| # | Test Case | Expected Result | Status |
|---|-----------|-----------------|--------|
| 1 | Log in with correct email and password | User is authenticated and redirected to the main app | ✅ Pass |
| 2 | Log in with incorrect password | Error message: "Invalid email or password" | ✅ Pass |
| 3 | Log in with an unregistered email | Error message: "Invalid email or password" | ✅ Pass |
| 4 | Access / without being logged in | Redirected to the login page | ✅ Pass |

#### Story 1.3 — User Logout ✅

> **As a** logged-in user, **I want to** log out **so that** my session is ended and my account is secured on shared devices.

| # | Test Case | Expected Result | Status |
|---|-----------|-----------------|--------|
| 1 | Navigate to /logout | Session is invalidated, user is redirected to login page with "You have been logged out" message | ✅ Pass |
| 2 | After logout, attempt to access / | Redirected to login page (session is gone) | ✅ Pass |

#### Story 1.4 — Admin Dashboard *(Iteration 2)*

> **As an** administrator, **I want to** view a dashboard of all registered users **so that** I can monitor platform usage and manage accounts.

| # | Test Case | Expected Result | Status |
|---|-----------|-----------------|--------|
| 1 | Log in as admin and navigate to Admin tab | Dashboard displays a table of all users (name, email, role, join date) and aggregate stats | Planned |
| 2 | Click "Delete" on a user row | User account is removed; table refreshes without that user | Planned |
| 3 | Log in as a regular user | Admin tab is not visible in navigation | Planned |
| 4 | Regular user attempts to access /api/admin/users directly | 403 Forbidden response | Planned |

#### Story 1.5 — Auth-Gated App Access ✅

> **As a** visitor (not logged in), **I want to** be redirected to the login page **so that** only authenticated users can access the app.

| # | Test Case | Expected Result | Status |
|---|-----------|-----------------|--------|
| 1 | Visit / without being logged in | Redirected to /login.html | ✅ Pass |
| 2 | Log in as a registered user | Redirected to the main app with Meal Plan, Grocery List, and Preferences tabs | ✅ Pass |
| 3 | Call any /api/* endpoint without a session | 401 Unauthorized JSON response | ✅ Pass |

---

### Epic 2: Onboarding and User Preferences

#### Story 2.1 — First-Time Onboarding Wizard ✅

> **As a** new user logging in for the first time, **I want to** be guided through a setup wizard **so that** SmartCart can personalize my meal plans from the start.

The onboarding wizard consists of 9 steps: Welcome, Household Size, Dietary Restrictions, Allergies, Cuisine Preferences, Disliked Foods, Meal Schedule, Pantry Staples, and a Review screen. Onboarding data is collected in memory and only persisted to the database after the user registers and logs in.

| # | Test Case | Expected Result | Status |
|---|-----------|-----------------|--------|
| 1 | New user logs in for the first time | Onboarding overlay appears over the main app | ✅ Pass |
| 2 | Complete all 9 steps and click "Create My First Meal Plan" | If logged in, preferences and pantry are saved and meal plan is generated. If not logged in, data is stashed in sessionStorage and user is redirected to register | ✅ Pass |
| 3 | Select "None" on the dietary restrictions step, then select a diet | "None" is deselected; the chosen diet is selected | ✅ Pass |
| 4 | Type "Brazilian, Peruvian" in the custom cuisine input and press Enter | Both "Brazilian" and "Peruvian" appear as active chips | ✅ Pass |
| 5 | On the Review step, click "Edit" next to Allergies | Wizard navigates back to the Allergies step with previous selections intact | ✅ Pass |
| 6 | Skip all optional steps (diets, allergies, cuisines, dislikes) and complete onboarding | Preferences are saved with empty/default values; meal plan is generated successfully | ✅ Pass |
| 7 | Guest completes onboarding, registers, then logs in | Stashed onboarding payload is automatically persisted to the new account | ✅ Pass |

#### Story 2.2 — Manage Preferences After Onboarding ✅

> **As a** user, **I want to** update my dietary restrictions, cuisine preferences, and serving size at any time **so that** future meal plans reflect my current needs.

| # | Test Case | Expected Result | Status |
|---|-----------|-----------------|--------|
| 1 | Navigate to Preferences, toggle a dietary restriction chip | Preference is saved immediately to the server | ✅ Pass |
| 2 | Change serving size from 2 to 4 | Serving size updates and persists on page reload | ✅ Pass |
| 3 | Type a custom cuisine "Peruvian" in the input and press Enter | "Peruvian" appears as a new active chip and is saved | ✅ Pass |
| 4 | Deselect a previously selected cuisine | Cuisine is removed from preferences and persists | ✅ Pass |

---

### Epic 3: AI-Powered Meal Plan Generation

#### Story 3.1 — Generate a Weekly Meal Plan ✅

> **As a** user, **I want to** generate a personalized weekly meal plan **so that** I have a full week of meals tailored to my dietary needs, preferred cuisines, and available pantry items.

| # | Test Case | Expected Result | Status |
|---|-----------|-----------------|--------|
| 1 | Click "Generate New Plan" with preferences set | A 7-day meal plan (breakfast, lunch, dinner) is generated and displayed in the meal grid | ✅ Pass |
| 2 | Generate a plan with "Vegetarian" dietary restriction | All generated recipes are vegetarian (no meat ingredients) | ✅ Pass |
| 3 | Generate a plan with "Peanuts" allergy | No recipe contains peanuts or peanut-derived ingredients | ✅ Pass |
| 4 | Generate a plan with "Italian, Japanese" as preferred cuisines and "Rotate" enabled | Meals rotate between Italian and Japanese across the week | ✅ Pass |
| 5 | Generate a plan with no Gemini API key configured | Error message displayed: "Failed to generate meal plan" | ✅ Pass |
| 6 | Generate a plan when the Gemini API returns malformed JSON | Error is caught; user sees "Failed to generate meal plan" instead of a crash | ✅ Pass |

#### Story 3.2 — View Recipe Details ✅

> **As a** user, **I want to** click on a meal in my weekly plan **so that** I can see the full recipe with ingredients, cook time, and step-by-step instructions.

| # | Test Case | Expected Result | Status |
|---|-----------|-----------------|--------|
| 1 | Click on a meal in the grid that has a linked recipe | Recipe detail panel shows title, cuisine, cook time, servings, ingredients list, and instructions | ✅ Pass |
| 2 | Click on a meal with no recipe yet (plan just generated, recipe not loaded) | Panel shows placeholder text: "Select a meal from the plan" | ✅ Pass |
| 3 | Click a different meal while a recipe is loading | The newly selected meal's recipe is displayed (stale response is discarded) | ✅ Pass |

---

### Epic 4: Grocery List Aggregation

#### Story 4.1 — View Consolidated Grocery List ✅

> **As a** user, **I want to** see a consolidated grocery list for my weekly meal plan **so that** I know exactly what to buy without duplicating ingredients across recipes.

| # | Test Case | Expected Result | Status |
|---|-----------|-----------------|--------|
| 1 | Navigate to Grocery List after generating a meal plan | Ingredients are listed, grouped by category (Protein, Dairy, Produce, Pantry) | ✅ Pass |
| 2 | Two recipes both require "chicken breast" | A single "chicken breast" entry appears with combined quantity | ✅ Pass |
| 3 | Navigate to Grocery List with no meal plan generated | Empty state message is shown | ✅ Pass |
| 4 | Check off an item on the grocery list | Item displays with a strikethrough / checked state | ✅ Pass |

---

### Epic 5: Database and Infrastructure

#### Story 5.1 — Database Schema Migrations ✅

> **As a** developer, **I want** the database schema to be version-controlled with Flyway migrations **so that** schema changes are applied consistently across all environments.

| # | Test Case | Expected Result | Status |
|---|-----------|-----------------|--------|
| 1 | Start the app against a fresh database | All 3 migrations run in order; all 9 tables are created | ✅ Pass |
| 2 | Start the app against a database that already has V1 and V2 | Only V3 is applied; existing data is preserved | ✅ Pass |
| 3 | A migration SQL file has a syntax error | App fails to start with a clear Flyway error; no partial migration is applied | ✅ Pass |

#### Story 5.2 — Pantry Item Management ✅

> **As a** user, **I want to** manage my pantry items **so that** the grocery list only includes ingredients I actually need to buy.

| # | Test Case | Expected Result | Status |
|---|-----------|-----------------|--------|
| 1 | During onboarding, toggle pantry staples (e.g., Olive Oil, Salt) | Selected items are saved and retrievable via the API | ✅ Pass |
| 2 | Type "soy sauce, flour" in the pantry custom input and press Enter | Both items are added to the pantry | ✅ Pass |
| 3 | Delete a pantry item via the API | Item is removed; subsequent GET /pantry no longer includes it | ✅ Pass |

---

## Technical Architecture

### Backend

- **Framework:** Spring Boot 4.0.3 (Java 17)
- **Database:** PostgreSQL (production) / H2 in-memory (dev profile)
- **Migrations:** Flyway (3 versioned migrations, 9 tables)
- **ORM:** Spring Data JPA with Hibernate (9 entities, 5 repositories)
- **AI Integration:** Google Gemini API via RestTemplate with structured JSON prompting
- **Authentication:** Session-based auth via `HttpSession` with BCrypt password hashing (jBCrypt). `AuthController` handles register/login/logout; `AuthService` validates credentials. All `/api/*` endpoints return 401 when no session exists.
- **Validation:** Enum-based validation for `DayOfWeek` and `MealType` fields

### Frontend

- **Technology:** Plain HTML, CSS, and JavaScript (no frameworks per course requirements)
- **Design System:** Custom CSS properties (HSL-based Atelier Salt palette), Google Fonts (Lora, Source Sans 3)
- **Architecture:** Single-page app with view switching via JS; `api.js` module for all backend communication
- **Onboarding:** 9-step wizard with progress dots, chip selectors, toggle grids, and tag inputs

### API Layer

All endpoints below require an authenticated session (return 401 otherwise).

| Area | Endpoints |
|------|-----------|
| Auth | GET /api/auth/me, POST /register, POST /login, GET /logout |
| Meal Plans | GET /api/meal-plan, POST /api/meal-plan/generate |
| Recipes | GET /api/recipes/:id |
| Grocery List | GET /api/grocery-list |
| Preferences | GET /api/preferences, PUT /api/preferences |
| Pantry | GET /api/pantry, POST /api/pantry, DELETE /api/pantry/:id |

---

## Work Distribution

| Team Member | Contributions |
|-------------|---------------|
| **Kelvin Li** | Database architecture (Flyway, JPA entities, PostgreSQL schema), REST API development, frontend translation from React to HTML/CSS/JS, onboarding wizard, preferences system, Gemini integration wiring, auth-gated API endpoints, onboarding-to-signup flow, debugging and QA |
| **Parsa Rahmatian** | Initial Gemini API integration and recipe generation prototype |
| **Kaden Nguyen** | Repository setup, Dockerfile, project scaffolding |
| **Andrei Reyes** | Authentication system (AuthController, AuthService, PasswordHasher, login/register pages, session management) |


---

## What Was Built in Iteration 1

1. **Full database schema** with 9 tables covering users, preferences, recipes, meal plans, grocery lists, and pantry items
2. **Flyway migration pipeline** ensuring consistent schema across dev (H2) and production (PostgreSQL) environments
3. **Session-based authentication** with registration, login, logout, password hashing (BCrypt), and all API endpoints gated behind 401 for unauthenticated requests
4. **13 REST API endpoints** covering auth, meal plans, recipes, grocery lists, preferences, and pantry management
5. **Gemini AI meal plan generator** that respects dietary restrictions, allergies, cuisine preferences, disliked foods, and pantry contents
6. **Responsive frontend** with three main views (Meal Plan grid, Grocery List, Preferences) and auth pages (login, register) built with plain HTML/CSS/JS
7. **9-step onboarding wizard** that personalizes the experience on first use, with comma-separated input, "None" options, custom cuisine entry, and a review screen. Onboarding data is stashed in sessionStorage for guests and persisted only after account creation
8. **Ingredient aggregation** logic that deduplicates and categorizes grocery items across all recipes in a meal plan
9. **Integration tests** validating the Gemini response parsing and database persistence pipeline
10. **Playwright MCP automated tests** verifying the complete registration, login, wrong-password, and duplicate-account flows

---

## Iteration 2 Priorities

- Admin dashboard and role-based views (admin vs. regular user)
- Deploy to Render with PostgreSQL
- Instacart API integration for grocery cart handoff
- Meal swap (regenerate individual meals without regenerating the entire week)
- Ingredient normalization and unit conversion for grocery aggregation
- Landing page for unauthenticated visitors
