package com._6.group4.smartcart.mealplanning;

import com._6.group4.smartcart.auth.SessionKeys;
import com._6.group4.smartcart.auth.User;
import com._6.group4.smartcart.auth.UserPreferences;
import com._6.group4.smartcart.auth.UserPreferencesRepository;
import com._6.group4.smartcart.auth.UserRepository;
import com._6.group4.smartcart.grocery.GroceryAggregationService;
import com._6.group4.smartcart.grocery.PantryItemRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MealPlanApiControllerPreferencesTest {

    private UserRepository userRepository;
    private UserPreferencesRepository preferencesRepository;
    private MealPlanApiController controller;

    @BeforeEach
    void setUp() {
        GeminiService geminiService = mock(GeminiService.class);
        RecipeRepository recipeRepository = mock(RecipeRepository.class);
        MealPlanRepository mealPlanRepository = mock(MealPlanRepository.class);
        userRepository = mock(UserRepository.class);
        preferencesRepository = mock(UserPreferencesRepository.class);
        PantryItemRepository pantryItemRepository = mock(PantryItemRepository.class);

        controller = new MealPlanApiController(
                geminiService,
                recipeRepository,
                mealPlanRepository,
                userRepository,
                preferencesRepository,
                pantryItemRepository,
                new GroceryAggregationService()
        );
    }

    @Test
    void updatePreferences_createsAndPersistsDietarySelections() {
        Long userId = 9L;
        User user = new User("prefs@example.com", "pw", "Prefs User");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(preferencesRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(preferencesRepository.save(any(UserPreferences.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, Object> update = new LinkedHashMap<>();
        update.put("servingSize", 4);
        update.put("dietaryRestrictions", "Vegetarian");
        update.put("allergies", "Peanut");
        update.put("preferredCuisines", "Mexican, Indian");
        update.put("rotateCuisines", false);
        update.put("dislikedFoods", "Olives");
        update.put("mealSchedule", "Breakfast, Dinner");
        update.put("preferredProteins", "Beans, Tofu");
        update.put("preferredVegetables", "Spinach, Broccoli");
        update.put("preferredFruits", "Apples");
        update.put("onboardingCompleted", true);

        ResponseEntity<?> response = controller.updatePreferences(update, session(userId));

        assertEquals(200, response.getStatusCode().value());

        ArgumentCaptor<UserPreferences> preferencesCaptor = ArgumentCaptor.forClass(UserPreferences.class);
        verify(preferencesRepository, atLeastOnce()).save(preferencesCaptor.capture());
        UserPreferences saved = preferencesCaptor.getValue();

        assertEquals(user, saved.getUser());
        assertEquals(4, saved.getServingSize());
        assertEquals("Vegetarian", saved.getDietaryRestrictions());
        assertEquals("Peanut", saved.getAllergies());
        assertEquals("Mexican, Indian", saved.getPreferredCuisines());
        assertFalse(saved.isRotateCuisines());
        assertEquals("Olives", saved.getDislikedFoods());
        assertEquals("Breakfast, Dinner", saved.getMealSchedule());
        assertEquals("Beans, Tofu", saved.getPreferredProteins());
        assertEquals("Spinach, Broccoli", saved.getPreferredVegetables());
        assertEquals("Apples", saved.getPreferredFruits());
        assertTrue(saved.isOnboardingCompleted());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getPreferences_returnsStoredServingSizeRestrictionsAndFlags() {
        Long userId = 9L;
        UserPreferences preferences = new UserPreferences(new User("prefs@example.com", "pw", "Prefs User"));
        preferences.setServingSize(3);
        preferences.setDietaryRestrictions("Vegan");
        preferences.setAllergies("Shellfish");
        preferences.setPreferredCuisines("Thai");
        preferences.setRotateCuisines(true);
        preferences.setDislikedFoods("Mushrooms");
        preferences.setMealSchedule("Lunch, Dinner");
        preferences.setPreferredProteins("Lentils");
        preferences.setPreferredVegetables("Cabbage");
        preferences.setPreferredFruits("Mango");
        preferences.setOnboardingCompleted(true);
        when(preferencesRepository.findByUserId(userId)).thenReturn(Optional.of(preferences));

        ResponseEntity<?> response = controller.getPreferences(session(userId));

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(3, body.get("servingSize"));
        assertEquals("Vegan", body.get("dietaryRestrictions"));
        assertEquals("Shellfish", body.get("allergies"));
        assertEquals("Thai", body.get("preferredCuisines"));
        assertEquals(Boolean.TRUE, body.get("rotateCuisines"));
        assertEquals("Mushrooms", body.get("dislikedFoods"));
        assertEquals("Lunch, Dinner", body.get("mealSchedule"));
        assertEquals("Lentils", body.get("preferredProteins"));
        assertEquals("Cabbage", body.get("preferredVegetables"));
        assertEquals("Mango", body.get("preferredFruits"));
        assertEquals(Boolean.TRUE, body.get("onboardingCompleted"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void updatePreferences_rejectsNonNumericServingSize() {
        Long userId = 9L;
        User user = new User("prefs@example.com", "pw", "Prefs User");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(preferencesRepository.findByUserId(userId)).thenReturn(Optional.of(new UserPreferences(user)));

        ResponseEntity<?> response = controller.updatePreferences(
                Map.of("servingSize", "four"),
                session(userId)
        );

        assertEquals(400, response.getStatusCode().value());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("servingSize must be a number", body.get("error"));
    }

    private HttpSession session(Long userId) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionKeys.USER_ID, userId);
        return session;
    }
}
