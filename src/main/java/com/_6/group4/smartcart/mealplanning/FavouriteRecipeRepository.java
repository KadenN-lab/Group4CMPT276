package com._6.group4.smartcart.mealplanning;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavouriteRecipeRepository extends JpaRepository<FavouriteRecipe, Long> {

    List<FavouriteRecipe> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<FavouriteRecipe> findByUserIdAndRecipeId(Long userId, Long recipeId);

    void deleteByUserIdAndRecipeId(Long userId, Long recipeId);

    boolean existsByUserIdAndRecipeId(Long userId, Long recipeId);
}
