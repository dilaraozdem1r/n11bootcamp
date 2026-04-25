package com.n11bootcamp.product_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(
        name = "product_translation",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "lang"}),
        indexes = {
                @Index(name = "idx_product_translation_lang", columnList = "lang"),
                @Index(name = "idx_product_translation_product", columnList = "product_id")
        }
)
public class ProductTranslation {

    public ProductTranslation() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore // ✅ sonsuz JSON döngüsünü kesin keser
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "lang", length = 10, nullable = false)
    private String lang; // tr,en,de,fr

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "tags", length = 1000)
    private String tags;

    @Column(name = "search_text")
    private String searchText;

    @Column(name = "material", length = 100)
    private String material;

    @Column(name = "product_type", length = 100)
    private String productType;

    @Column(name = "category_name", length = 100)
    private String categoryName;

    // getters/setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getLang() { return lang; }
    public void setLang(String lang) { this.lang = lang; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getSearchText() { return searchText; }
    public void setSearchText(String searchText) { this.searchText = searchText; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}
