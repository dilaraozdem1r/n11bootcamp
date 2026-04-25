package com.n11bootcamp.shopping_cart_service.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

@Entity
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    // ?? Primary key. Otomatik artan ID. (AUTO => DB’ye göre uygun strateji seçilir)

    @Column(unique = true)
    private String shoppingCartName;
    // ?? Her alışveriş sepetinin kendine özgü ismi var (ör: "Sepet-123").
    // unique = true => Aynı isimde başka sepet olamaz.

    /**
     * ?? Many-to-Many İlişki:
     * - Bir sepetin içinde birden fazla ürün olabilir.
     * - Aynı ürün birden fazla sepette bulunabilir.
     *
     * @JoinTable:
     *  - İlişkiyi tutacak ara tabloyu tanımlar (shopping_cart_product).
     *  - joinColumns = Bu entity’nin (ShoppingCart) ID’sini temsil eder (shopping_cart_id).
     *  - inverseJoinColumns = Karşı entity’nin (Product) ID’sini temsil eder (product_id).
     *
     * Sonuç: DB’de şu tablo oluşur ??
     *  shopping_cart_product
     *  ---------------------------
     *  shopping_cart_id | product_id
     *  1                | 101
     *  1                | 102
     *  2                | 101
     */
    @ManyToMany
    @JoinTable(
            name = "shopping_cart_product", // ?? Ara tablo ismi
            joinColumns = @JoinColumn(name = "shopping_cart_id"), // ?? Bu entity’nin FK’sı
            inverseJoinColumns = @JoinColumn(name = "product_id") // ?? Product entity FK’sı
    )
    private Set<Product> products;

    // -------------------- GETTER & SETTER --------------------

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getShoppingCartName() {
        return shoppingCartName;
    }
    public void setShoppingCartName(String shoppingCartName) {
        this.shoppingCartName = shoppingCartName;
    }

    public Set<Product> getProducts() {
        return products;
    }
    public void setProducts(Set<Product> products) {
        this.products = products;
    }
}