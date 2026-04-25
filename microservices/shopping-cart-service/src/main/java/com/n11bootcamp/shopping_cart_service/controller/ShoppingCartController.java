package com.n11bootcamp.shopping_cart_service.controller;


import java.util.List;
import java.util.Map;


import com.n11bootcamp.shopping_cart_service.entity.Product;
import com.n11bootcamp.shopping_cart_service.entity.ShoppingCart;
import com.n11bootcamp.shopping_cart_service.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://85.159.71.66:3000", "http://94.73.134.50:3000"})
@RequestMapping("api/shopping-cart")
public class ShoppingCartController  {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping
    public ResponseEntity<ShoppingCart> createCart(@RequestParam("name") String name) {
        return shoppingCartService.createCart(name);
    }

    @PostMapping("{id}")
    public ResponseEntity<ShoppingCart> addProductsToCart(
            @PathVariable("id") Long shoppingCartId,
            @RequestBody List<Product> products) {
        return shoppingCartService.addProducts(shoppingCartId, products);
    }

    @DeleteMapping("/{id}/products/{productId}")
    public ResponseEntity<ShoppingCart> removeProduct(
            @PathVariable("id") Long shoppingCartId,
            @PathVariable("productId") Long productId) {
        return shoppingCartService.removeProduct(shoppingCartId, productId);
    }

    // ✅ (compile fix) signature yine Long tek parametre
    @GetMapping("/totalprice/{id}")
    public ResponseEntity<Map<String, String>> getTotalPrice(
            @PathVariable("id") Long shoppingCartId) {
        return shoppingCartService.getShoppingCartPrice(shoppingCartId);
    }

    // ✅ i18n: Accept-Language al ve localize edilmiş sepet dön
    @GetMapping("{id}")
    public ResponseEntity<ShoppingCart> getCartById(
            @PathVariable("id") Long shoppingCartId,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage
    ) {
        return shoppingCartService.getCartById(shoppingCartId, acceptLanguage);
    }

    // ✅ i18n
    @GetMapping("/by-name/{name}")
    public ResponseEntity<ShoppingCart> getCartByShoppingCartName(
            @PathVariable("name") String shoppingCartName,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage
    ) {
        return shoppingCartService.getCartByShoppingCartName(shoppingCartName, acceptLanguage);
    }

    // ✅ i18n
    @GetMapping
    public ResponseEntity<List<ShoppingCart>> getAllCarts(
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage
    ) {
        return shoppingCartService.getAllCarts(acceptLanguage);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteCartById(@PathVariable("id") Long shoppingCartId) {
        return shoppingCartService.deleteCartById(shoppingCartId);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAllCarts() {
        return shoppingCartService.deleteAllCarts();
    }
}