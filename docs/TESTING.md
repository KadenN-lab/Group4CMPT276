# Testing Matrix

This project tracks testing at the story level for the implemented epics below. Epic 4 (Instacart integration) is intentionally excluded because that feature has been deferred.

## Epic 1: Authentication and User Management

### Story: users can register and log in securely
- Automated:
  - `src/test/java/com/_6/group4/smartcart/auth/AuthControllerTest.java`
  - `src/test/java/com/_6/group4/smartcart/auth/PasswordHasherTest.java`
  - `src/test/java/com/_6/group4/smartcart/infrastructure/SecurityConfigTest.java`
- Manual:
  - Register a new user and confirm redirect to `login.html?registered=1`.
  - Log in with valid credentials and confirm redirect to `app.html`.
  - Attempt login with an invalid password and confirm the error stays on the login flow.
  - Confirm logout clears the session and protected API calls return `401`.

### Story: users can store dietary preferences, serving sizes, and restrictions
- Automated:
  - `src/test/java/com/_6/group4/smartcart/mealplanning/MealPlanApiControllerPreferencesTest.java`
- Manual:
  - Complete onboarding with serving size, dietary restrictions, cuisine preferences, disliked foods, and meal schedule.
  - Refresh the app and confirm the saved preferences repopulate correctly.
  - Update one preference only and confirm unrelated preference fields remain unchanged.

## Epic 2: Intelligent Meal Planning and Recipe Generation

### Story: users can generate a weekly meal plan from constraints
- Automated:
  - `src/test/java/com/_6/group4/smartcart/mealplanning/GeminiServiceTest.java`
  - `src/test/java/com/_6/group4/smartcart/mealplanning/GeminiPipelineDemoTest.java`
  - `src/test/java/com/_6/group4/smartcart/mealplanning/MealPlanGenerationSupportTest.java`
  - `src/test/java/com/_6/group4/smartcart/mealplanning/dto/GeminiJsonParsingTest.java`
- Manual:
  - Generate a meal plan with different serving sizes and dietary restrictions.
  - Confirm the returned plan contains a full week of expected meal slots.
  - Confirm malformed Gemini output does not break the UI and produces a user-facing error when generation fails.

### Story: the system validates dietary compliance and users can swap individual meals
- Automated:
  - `src/test/java/com/_6/group4/smartcart/mealplanning/AllergyVerificationTest.java`
  - `src/test/java/com/_6/group4/smartcart/mealplanning/MealPlanApiControllerSwapTest.java`
- Manual:
  - Swap a single meal and confirm only that slot changes.
  - Try swapping a meal while an allergy is configured and confirm the replacement does not include the banned ingredient.
  - Confirm the rest of the week stays unchanged after a partial swap.

## Epic 3: Ingredient Normalization and Grocery Aggregation

### Story: duplicate ingredients are merged, normalized, and pantry items are subtracted
- Automated:
  - `src/test/java/com/_6/group4/smartcart/grocery/IngredientNormalizerTest.java`
  - `src/test/java/com/_6/group4/smartcart/grocery/GroceryAggregationServiceTest.java`
  - `src/test/java/com/_6/group4/smartcart/mealplanning/MealPlanApiControllerPantryTest.java`
  - `src/test/java/com/_6/group4/smartcart/mealplanning/GroceryAggregationTest.java`
- Manual:
  - Generate a weekly plan with overlapping ingredients and confirm the grocery list consolidates duplicate items.
  - Add pantry amounts inline and confirm the remaining grocery quantity updates correctly.
  - Mark an unknown-quantity pantry item as already owned and confirm it moves to the covered section.

## Epic 5: Database Architecture, Deployment, and Infrastructure Management

### Story: schema, migrations, environment configuration, and deployment remain stable
- Automated:
  - `src/test/java/com/_6/group4/Group4ApplicationTests.java`
  - `src/test/java/com/_6/group4/smartcart/infrastructure/SecurityConfigTest.java`
  - `src/test/java/com/_6/group4/smartcart/infrastructure/FlywayMigrationLayoutTest.java`
- Manual:
  - Run the app against the configured database and confirm startup completes with Flyway migrations applied.
  - Verify seeded admin access works after deployment.
  - Check Render logs for clean boot, datasource startup, and no Flyway validation errors.
