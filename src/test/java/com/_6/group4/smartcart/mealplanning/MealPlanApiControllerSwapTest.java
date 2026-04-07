package com._6.group4.smartcart.mealplanning;

import com._6.group4.smartcart.auth.SessionKeys;
import com._6.group4.smartcart.auth.User;
import com._6.group4.smartcart.auth.UserPreferences;
import com._6.group4.smartcart.auth.UserPreferencesRepository;
import com._6.group4.smartcart.auth.UserRepository;
import com._6.group4.smartcart.grocery.GroceryAggregationService;
import com._6.group4.smartcart.grocery.PantryItemRepository;
import com._6.group4.smartcart.mealplanning.dto.GeminiRecipeDto;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MealPlanApiControllerSwapTest {

    private GeminiService geminiService;
    private RecipeRepository recipeRepository;
    private MealPlanRepository mealPlanRepository;
    private UserPreferencesRepository preferencesRepository;
    private MealPlanApiController controller;

    @BeforeEach
    void setUp() {
        geminiService = mock(GeminiService.class);
        recipeRepository = mock(RecipeRepository.class);
        mealPlanRepository = mock(MealPlanRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        preferencesRepository = mock(UserPreferencesRepository.class);
        PantryItemRepository pantryItemRepository = mock(PantryItemRepository.class);
        FavouriteRecipeRepository favouriteRecipeRepository = mock(FavouriteRecipeRepository.class);

        controller = new MealPlanApiController(
                geminiService,
                recipeRepository,
                mealPlanRepository,
                userRepository,
                preferencesRepository,
                pantryItemRepository,
                new GroceryAggregationService(),
                favouriteRecipeRepository
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void swapMeals_replacesRequestedSlotAndKeepsOtherMealsUntouched() {
        Long userId = 42L;
        MealPlan plan = mealPlan(
                slot(DayOfWeek.MONDAY, MealType.DINNER, "Old Dinner"),
                slot(DayOfWeek.TUESDAY, MealType.LUNCH, "Keep Lunch")
        );
        UserPreferences preferences = new UserPreferences(new User("swap@example.com", "pw", "Swap User"));
        preferences.setServingSize(4);
        preferences.setDietaryRestrictions("Vegetarian");
        preferences.setPreferredCuisines("Indian");

        GeminiRecipeDto replacement = new GeminiRecipeDto(
                "New Dinner",
                "Indian",
                30,
                4,
                "Cook and serve",
                List.of(new GeminiRecipeDto.IngredientDto("Paneer", 2.0, "cup"))
        );

        when(mealPlanRepository.findTopByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Optional.of(plan));
        when(preferencesRepository.findByUserId(userId)).thenReturn(Optional.of(preferences));
        when(geminiService.generateSingleMeal("MONDAY", "DINNER", null, "Vegetarian", "Indian"))
                .thenReturn(replacement);
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = controller.swapMeals(
                Map.of("slots", List.of(Map.of("dayOfWeek", "MONDAY", "mealType", "DINNER"))),
                session(userId)
        );

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(List.of("MONDAY DINNER"), body.get("swapped"));
        assertFalse(body.containsKey("swapFailed"));
        assertEquals(2, plan.getRecipes().size());
        assertEquals("New Dinner", titleFor(plan, DayOfWeek.MONDAY, MealType.DINNER));
        assertEquals("Keep Lunch", titleFor(plan, DayOfWeek.TUESDAY, MealType.LUNCH));
        verify(geminiService).generateSingleMeal("MONDAY", "DINNER", null, "Vegetarian", "Indian");
        verify(mealPlanRepository).save(plan);
    }

    @Test
    @SuppressWarnings("unchecked")
    void swapMeals_reportsFailureWhenReplacementStillContainsAllergen() {
        Long userId = 42L;
        MealPlan plan = mealPlan(slot(DayOfWeek.MONDAY, MealType.DINNER, "Old Dinner"));
        UserPreferences preferences = new UserPreferences(new User("swap@example.com", "pw", "Swap User"));
        preferences.setServingSize(2);
        preferences.setAllergies("peanut");

        GeminiRecipeDto allergenRecipe = new GeminiRecipeDto(
                "Peanut Stir Fry",
                "Asian",
                25,
                2,
                "Cook",
                List.of(new GeminiRecipeDto.IngredientDto("Peanut butter", 2.0, "tbsp"))
        );

        when(mealPlanRepository.findTopByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Optional.of(plan));
        when(preferencesRepository.findByUserId(userId)).thenReturn(Optional.of(preferences));
        when(geminiService.generateSingleMeal(eq("MONDAY"), eq("DINNER"), eq("peanut"), eq(null), eq(null)))
                .thenReturn(allergenRecipe, allergenRecipe);

        ResponseEntity<?> response = controller.swapMeals(
                Map.of("slots", List.of(Map.of("dayOfWeek", "MONDAY", "mealType", "DINNER"))),
                session(userId)
        );

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(List.of(), body.get("swapped"));
        assertEquals(List.of("MONDAY DINNER (allergen detected)"), body.get("swapFailed"));
        assertEquals("Old Dinner", titleFor(plan, DayOfWeek.MONDAY, MealType.DINNER));
        verify(geminiService, times(2)).generateSingleMeal("MONDAY", "DINNER", "peanut", null, null);
        verify(mealPlanRepository).save(plan);
    }

    private HttpSession session(Long userId) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionKeys.USER_ID, userId);
        return session;
    }

    private MealPlan mealPlan(MealPlanRecipe... slots) {
        User user = new User("planner@example.com", "pw", "Planner");
        MealPlan plan = new MealPlan(user, LocalDate.of(2026, 4, 6));
        for (MealPlanRecipe slot : slots) {
            slot.setMealPlan(plan);
            plan.getRecipes().add(slot);
        }
        return plan;
    }

    private MealPlanRecipe slot(DayOfWeek dayOfWeek, MealType mealType, String title) {
        Recipe recipe = new Recipe(title);
        return new MealPlanRecipe(null, recipe, dayOfWeek, mealType);
    }

    private String titleFor(MealPlan plan, DayOfWeek dayOfWeek, MealType mealType) {
        return plan.getRecipes().stream()
                .filter(slot -> slot.getDayOfWeek() == dayOfWeek && slot.getMealType() == mealType)
                .findFirst()
                .map(slot -> slot.getRecipe().getTitle())
                .orElse(null);
    }
}
