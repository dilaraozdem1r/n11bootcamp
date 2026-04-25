package com.n11bootcamp.shopping_cart_service.service;

import com.n11bootcamp.shopping_cart_service.entity.Product;
import com.n11bootcamp.shopping_cart_service.entity.ShoppingCart;
import com.n11bootcamp.shopping_cart_service.repository.ProductRepository;
import com.n11bootcamp.shopping_cart_service.repository.ShoppingCartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RestTemplate restTemplate;

    // ✅ Microservice discovery kullanıyorsan:
    private static final String PRODUCT_SERVICE_BASE = "http://PRODUCT-SERVICE";
    // ✅ Local test için istersen bunu açıp kapatabilirsin:
    // private static final String PRODUCT_SERVICE_BASE = "http://localhost:8764";

    public ResponseEntity<ShoppingCart> createCart(String name) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setShoppingCartName(name);
        return ResponseEntity.ok().body(shoppingCartRepository.save(shoppingCart));
    }

    public ResponseEntity<ShoppingCart> addProducts(Long shoppingCartId, List<Product> products) {

        ShoppingCart shoppingCart = shoppingCartRepository.findById(shoppingCartId)
                .orElseThrow(() -> new RuntimeException("Shopping cart not found"));

        // ✅ Ürünleri güvenli şekilde upsert et (NULL overwrite yok)
        List<Product> persistedProducts = new ArrayList<>();

        for (Product incoming : products) {
            if (incoming == null) continue;

            Product entity = productRepository.findById(incoming.getId())
                    .orElseGet(() -> {
                        Product p = new Product();
                        p.setId(incoming.getId());
                        return p;
                    });

            if (incoming.getTitle() != null && !incoming.getTitle().isBlank()) {
                entity.setTitle(incoming.getTitle());
            }
            if (incoming.getCategory() != null && !incoming.getCategory().isBlank()) {
                entity.setCategory(incoming.getCategory());
            }
            if (incoming.getImg() != null && !incoming.getImg().isBlank()) {
                entity.setImg(incoming.getImg());
            }
            if (incoming.getLabels() != null && !incoming.getLabels().isBlank()) {
                entity.setLabels(incoming.getLabels());
            }
            if (incoming.getDescription() != null && !incoming.getDescription().isBlank()) {
                entity.setDescription(incoming.getDescription());
            }
            if (incoming.getPrice() > 0) {
                entity.setPrice(incoming.getPrice());
            }

            Product saved = productRepository.saveAndFlush(entity);
            persistedProducts.add(saved);
        }

        Set<Product> existingProducts = shoppingCart.getProducts();
        if (existingProducts == null) existingProducts = new HashSet<>();
        existingProducts.addAll(persistedProducts);

        shoppingCart.setProducts(existingProducts);
        return ResponseEntity.ok().body(shoppingCartRepository.save(shoppingCart));
    }

    public ResponseEntity<ShoppingCart> removeProduct(Long shoppingCartId, Long productId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findById(shoppingCartId)
                .orElseThrow(() -> new RuntimeException("Shopping cart not found"));

        Set<Product> existingProducts = shoppingCart.getProducts();
        if (existingProducts == null) return ResponseEntity.ok().body(shoppingCart);

        existingProducts.removeIf(product -> product.getId() == productId);
        shoppingCart.setProducts(existingProducts);

        return ResponseEntity.ok().body(shoppingCartRepository.save(shoppingCart));
    }

    // ✅ Controller compile fix için tek parametreli overload kalsın
    public ResponseEntity<Map<String, String>> getShoppingCartPrice(Long shoppingCartId) {
        return getShoppingCartPrice(shoppingCartId, null);
    }

    // ✅ İstersen header'a göre ileride para birimi vs. de eklenebilir
    public ResponseEntity<Map<String, String>> getShoppingCartPrice(Long shoppingCartId, String acceptLanguage) {
        Map<String, String> response = new HashMap<>();

        ShoppingCart shoppingCart = shoppingCartRepository.findById(shoppingCartId)
                .orElseThrow(() -> new RuntimeException("Shopping cart not found"));

        int totalPrice = shoppingCart.getProducts()
                .stream()
                .map(product -> restTemplate.getForObject(
                        PRODUCT_SERVICE_BASE + "/api/product/" + product.getId(), HashMap.class))
                .mapToInt(productResponse -> (int) productResponse.get("price"))
                .sum();

        response.put("total_price", Double.toString(totalPrice));
        return ResponseEntity.ok().body(response);
    }

    // ✅ i18n: sepeti localize ederek dön
    public ResponseEntity<ShoppingCart> getCartById(Long shoppingCartId, String acceptLanguage) {
        ShoppingCart shoppingCart = shoppingCartRepository.findById(shoppingCartId)
                .orElseThrow(() -> new RuntimeException("Shopping cart not found"));

        localizeCart(shoppingCart, acceptLanguage);
        return ResponseEntity.ok(shoppingCart);
    }

    // ✅ i18n: name ile de localize
    public ResponseEntity<ShoppingCart> getCartByShoppingCartName(String shoppingCartName, String acceptLanguage) {
        Optional<ShoppingCart> opt = shoppingCartRepository.findByShoppingCartName(shoppingCartName);

        if (opt.isPresent()) {
            ShoppingCart cart = opt.get();
            localizeCart(cart, acceptLanguage);
            return ResponseEntity.ok(cart);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // ✅ i18n: list
    public ResponseEntity<List<ShoppingCart>> getAllCarts(String acceptLanguage) {
        List<ShoppingCart> shoppingCarts = shoppingCartRepository.findAll();
        shoppingCarts.forEach(c -> localizeCart(c, acceptLanguage));
        return ResponseEntity.ok(shoppingCarts);
    }

    public ResponseEntity<String> deleteCartById(Long shoppingCartId) {
        if (shoppingCartRepository.existsById(shoppingCartId)) {
            shoppingCartRepository.deleteById(shoppingCartId);
            return ResponseEntity.ok("Shopping Cart deleted successfully");
        } else {
            throw new RuntimeException("Shopping Cart not found in DB");
        }
    }

    public ResponseEntity<String> deleteAllCarts() {
        shoppingCartRepository.deleteAll();
        return ResponseEntity.ok("All Shopping Carts deleted successfully");
    }

    // ---------------------------
    // ✅ CORE: translations[] içinden lang’e göre seçip Product entity’yi doldurur
    // ---------------------------
    @SuppressWarnings("unchecked")
    private void localizeCart(ShoppingCart cart, String acceptLanguage) {
        if (cart == null || cart.getProducts() == null || cart.getProducts().isEmpty()) return;

        String lang = normalizeLang(acceptLanguage);

        for (Product p : cart.getProducts()) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", lang);
                HttpEntity<Void> req = new HttpEntity<>(headers);

                ResponseEntity<Map> resp = restTemplate.exchange(
                        PRODUCT_SERVICE_BASE + "/api/product/" + p.getId(),
                        HttpMethod.GET,
                        req,
                        Map.class
                );

                Map body = resp.getBody();
                if (body == null) continue;

                // translations list
                Object translationsObj = body.get("translations");
                if (!(translationsObj instanceof List)) continue;

                List<Map<String, Object>> translations = (List<Map<String, Object>>) translationsObj;
                Map<String, Object> chosen = pickTranslation(translations, lang);

                if (chosen == null) continue;

                Object tTitle = chosen.get("title");
                Object tDesc = chosen.get("description");
                Object tCategoryName = chosen.get("categoryName");

                if (tTitle instanceof String s && !s.isBlank()) p.setTitle(s);
                if (tDesc instanceof String s && !s.isBlank()) p.setDescription(s);
                if (tCategoryName instanceof String s && !s.isBlank()) p.setCategory(s);

                // categoryKey'yi root'tan al (response’da var)
                Object categoryKey = body.get("categoryKey");
                if (categoryKey instanceof String) {
                    // entity’de alan yoksa ignore (senin entity’de yoktu)
                    // eğer eklediysen burada set edebilirsin
                }

            } catch (Exception e) {
                // Sessizce yutma: debug için görünsün
                System.out.println("Product-service i18n fetch failed for id=" + p.getId()
                        + " lang=" + lang + " err=" + e.getMessage());
            }
        }
    }

    private String normalizeLang(String acceptLanguage) {
        if (acceptLanguage == null || acceptLanguage.isBlank()) return "tr";
        // "en-US,en;q=0.9" -> "en"
        String first = acceptLanguage.split(",")[0].trim();
        if (first.contains("-")) first = first.substring(0, first.indexOf('-'));
        return first.isBlank() ? "tr" : first;
    }

    private Map<String, Object> pickTranslation(List<Map<String, Object>> translations, String lang) {
        if (translations == null || translations.isEmpty()) return null;

        // 1) exact lang
        for (Map<String, Object> t : translations) {
            Object l = t.get("lang");
            if (l != null && lang.equalsIgnoreCase(l.toString())) return t;
        }
        // 2) fallback tr
        for (Map<String, Object> t : translations) {
            Object l = t.get("lang");
            if (l != null && "tr".equalsIgnoreCase(l.toString())) return t;
        }
        // 3) first
        return translations.get(0);
    }
}
