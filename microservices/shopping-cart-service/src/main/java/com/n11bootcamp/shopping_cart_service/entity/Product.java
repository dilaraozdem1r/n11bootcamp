package com.n11bootcamp.shopping_cart_service.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;

@Entity
public class Product {

    @Id
    private long id;

    private String title;
    private String img;
    private String labels;
    private long price;
    private String description;

    // DB NOT NULL
    private String category;

    @Column(name = "category_key")
    private String categoryKey;

    /**
     * ✅ product-service response'unda translations array var.
     * Bunu DB'ye yazmayacağız, sadece title/description doldurmak için kullanacağız.
     */
    @Transient
    @JsonProperty("translations")
    private JsonNode translations;

    @ManyToMany(mappedBy = "products")
    private Set<ShoppingCart> shoppingCarts;

    @PrePersist
    @PreUpdate
    private void fillRequiredFields() {

        // ✅ category boşsa categoryKey'den doldur
        if ((category == null || category.isBlank()) &&
                (categoryKey != null && !categoryKey.isBlank())) {
            category = categoryKey;
        }

        // ✅ title boşsa translations[0].title'dan doldur
        if (title == null || title.isBlank()) {
            String t = firstTranslationText("title");
            if (t != null && !t.isBlank()) title = t;
        }

        // ✅ description boşsa translations[0].description'dan doldur (opsiyonel ama iyi)
        if (description == null || description.isBlank()) {
            String d = firstTranslationText("description");
            if (d != null && !d.isBlank()) description = d;
        }

        // ✅ en son çare: title hâlâ boşsa kırılmasın diye default ver
        if (title == null || title.isBlank()) {
            title = "product-" + id;
        }

        // ✅ category hâlâ boşsa default
        if (category == null || category.isBlank()) {
            category = "unknown";
        }
    }

    private String firstTranslationText(String field) {
        try {
            if (translations == null || !translations.isArray() || translations.isEmpty()) return null;
            JsonNode first = translations.get(0);
            if (first == null) return null;
            JsonNode node = first.get(field);
            if (node == null || node.isNull()) return null;
            return node.asText();
        } catch (Exception e) {
            return null;
        }
    }

    // --- getters/setters ---

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }

    public String getLabels() { return labels; }
    public void setLabels(String labels) { this.labels = labels; }

    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCategoryKey() { return categoryKey; }
    public void setCategoryKey(String categoryKey) { this.categoryKey = categoryKey; }

    public JsonNode getTranslations() { return translations; }
    public void setTranslations(JsonNode translations) { this.translations = translations; }

    @JsonIgnore
    public Set<ShoppingCart> getShoppingCarts() { return shoppingCarts; }
    public void setShoppingCarts(Set<ShoppingCart> shoppingCarts) { this.shoppingCarts = shoppingCarts; }
}