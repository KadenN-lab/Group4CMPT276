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
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FavouriteRecipeTest {

    private RecipeRepository recipeRepository;
    private UserRepository userRepository;
    private FavouriteRecipeRepository favouriteRecipeRepository;
    private MealPlanApiController controller;

    @BeforeEach
    void setUp() {
        GeminiService geminiService = mock(GeminiService.class);
        recipeRepository = mock(RecipeRepository.class);
        MealPlanRepository mealPlanRepository = mock(MealPlanRepository.class);
        userRepository = mock(UserRepository.class);
        UserPreferencesRepository preferencesRepository = mock(UserPreferencesRepository.class);
        PantryItemRepository pantryItemRepository = mock(PantryItemRepository.class);
        favouriteRecipeRepository = mock(FavouriteRecipeRepository.class);

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
    void toggleFavourite_addsWhenNotExists() {
        Long userId = 1L;
        Long recipeId = 10L;
        Recipe recipe = new Recipe("Test Recipe");
        User user = new User("test@example.com", "pw", "Test");

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(favouriteRecipeRepository.existsByUserIdAndRecipeId(userId, recipeId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(favouriteRecipeRepository.save(any(FavouriteRecipe.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<?> response = controller.toggleFavourite(recipeId, session(userId));

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("favourited"));
        verify(favouriteRecipeRepository).save(any(FavouriteRecipe.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void toggleFavourite_removesWhenExists() {
        Long userId = 1L;
        Long recipeId = 10L;
        Recipe recipe = new Recipe("Test Recipe");

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(favouriteRecipeRepository.existsByUserIdAndRecipeId(userId, recipeId)).thenReturn(true);

        ResponseEntity<?> response = controller.toggleFavourite(recipeId, session(userId));

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertFalse((Boolean) body.get("favourited"));
        verify(favouriteRecipeRepository).deleteByUserIdAndRecipeId(userId, recipeId);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getFavourites_returnsUserFavourites() {
        Long userId = 1L;
        Recipe recipe1 = new Recipe("Recipe One");
        Recipe recipe2 = new Recipe("Recipe Two");
        User user = new User("test@example.com", "pw", "Test");

        FavouriteRecipe fav1 = new FavouriteRecipe(user, recipe1);
        FavouriteRecipe fav2 = new FavouriteRecipe(user, recipe2);

        when(favouriteRecipeRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(List.of(fav1, fav2));

        ResponseEntity<?> response = controller.getFavourites(session(userId));

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        List<Map<String, Object>> favourites = (List<Map<String, Object>>) body.get("favourites");
        assertEquals(2, favourites.size());
        assertEquals("Recipe One", favourites.get(0).get("recipeTitle"));
        assertEquals("Recipe Two", favourites.get(1).get("recipeTitle"));
    }

    @Test
    void toggleFavourite_requiresAuth() {
        ResponseEntity<?> response = controller.toggleFavourite(10L, new MockHttpSession());

        assertEquals(401, response.getStatusCode().value());
        verifyNoInteractions(favouriteRecipeRepository);
    }

    private HttpSession session(Long userId) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionKeys.USER_ID, userId);
        return session;
    }
}
