package com._6.group4.smartcart.mealplanning;

import com._6.group4.smartcart.auth.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favourite_recipes", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "recipe_id"}))
public class FavouriteRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    protected FavouriteRecipe() {}

    public FavouriteRecipe(User user, Recipe recipe) {
        this.user = user;
        this.recipe = recipe;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Recipe getRecipe() { return recipe; }
    public void setRecipe(Recipe recipe) { this.recipe = recipe; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
