package com._6.group4.smartcart.mealplanning;

import jakarta.persistence.*;

@Entity
@Table(name = "meal_plan_recipes")
public class MealPlanRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_plan_id", nullable = false)
    private MealPlan mealPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(name = "day_of_week", nullable = false, length = 10)
    private String dayOfWeek;

    @Column(name = "meal_type", nullable = false, length = 10)
    private String mealType;

    protected MealPlanRecipe() {}

    public MealPlanRecipe(MealPlan mealPlan, Recipe recipe, String dayOfWeek, String mealType) {
        this.mealPlan = mealPlan;
        this.recipe = recipe;
        this.dayOfWeek = dayOfWeek;
        this.mealType = mealType;
    }

    public Long getId() { return id; }
    public MealPlan getMealPlan() { return mealPlan; }
    public void setMealPlan(MealPlan mealPlan) { this.mealPlan = mealPlan; }
    public Recipe getRecipe() { return recipe; }
    public void setRecipe(Recipe recipe) { this.recipe = recipe; }
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }
}
