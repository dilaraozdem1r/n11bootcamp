package com.n11bootcamp.product_service.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "product",
        indexes = {
                @Index(name = "idx_products_brand", columnList = "brand"),
                @Index(name = "idx_products_color", columnList = "color"),
                @Index(name = "idx_products_labels", columnList = "labels"),
                @Index(name = "idx_products_category_key", columnList = "category_key")
        }
)
public class Product {

    public Product() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "price", nullable = false)
    private long price;

    @Column(name = "img", length = 512)
    private String img;

    @Column(name = "labels", length = 255)
    private String labels;

    @Column(name = "brand", length = 255)
    private String brand;

    @Column(name = "color", length = 100)
    private String color;

    /**
     * ✅ DB FIX (legacy):
     * product.title NOT NULL ise burada tutulmalı
     */
    @Column(name = "title", nullable = false, length = 255)
    private String title = "-";

    /**
     * ✅ DB FIX (legacy):
     * product.category NOT NULL ise burada tutulmalı
     */
    @Column(name = "category", nullable = false, length = 255)
    private String category = "giysi";

    /**
     * ✅ NEW (legacy):
     * Eğer DB’de product.description kolonu varsa buraya eklenebilir.
     * i18n sisteminde asıl açıklama translation tablosunda tutulmalı.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Dil bağımsız key
    @Column(name = "category_key", length = 100)
    private String categoryKey;

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<ProductTranslation> translations = new ArrayList<>();

    public void addTranslation(ProductTranslation t) {
        t.setProduct(this);
        this.translations.add(t);
    }

    // ======================
    // getters/setters
    // ======================

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }

    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }

    public String getLabels() { return labels; }
    public void setLabels(String labels) { this.labels = labels; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    // ✅ NEW description getter/setter
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategoryKey() { return categoryKey; }
    public void setCategoryKey(String categoryKey) { this.categoryKey = categoryKey; }

    public List<ProductTranslation> getTranslations() { return translations; }
    public void setTranslations(List<ProductTranslation> translations) {
        this.translations = translations;
    }
}