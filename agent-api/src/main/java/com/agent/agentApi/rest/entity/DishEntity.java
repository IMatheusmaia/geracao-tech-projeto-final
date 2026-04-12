package com.agent.agentApi.rest.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@RedisHash("dish")
public class DishEntity {

    @Id
    private String id;

    private String title;
    private String description;

    @Indexed
    private String category;

    private BigDecimal price;

    @JsonProperty("image_url")
    private String imageUrl;

    private List<String> ingredients = new ArrayList<>();

    @CreatedDate
    @JsonProperty("saved_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Instant savedAt;

    @LastModifiedDate
    @JsonProperty("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Instant updatedAt;

    public DishEntity() {}

    public DishEntity(String id, String title, String description, String category,
                      BigDecimal price, String imageUrl, List<String> ingredients) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
        this.imageUrl = imageUrl;
        if (ingredients != null) this.ingredients = ingredients;
    }

    // --- Getters ---
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public BigDecimal getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public List<String> getIngredients() { return ingredients; }
    public Instant getSavedAt() { return savedAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    // --- Setters ---
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients != null ? ingredients : new ArrayList<>(); }
    
}