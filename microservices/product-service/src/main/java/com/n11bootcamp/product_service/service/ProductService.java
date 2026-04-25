package com.n11bootcamp.product_service.service;


import com.n11bootcamp.product_service.dto.ProductTranslationRequest;
import com.n11bootcamp.product_service.entity.Product;
import com.n11bootcamp.product_service.entity.ProductTranslation;
import com.n11bootcamp.product_service.repository.ProductRepository;
import com.n11bootcamp.product_service.repository.ProductTranslationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ProductTranslationRepository translationRepository;

    private final Logger logger = LoggerFactory.getLogger(ProductService.class);

    // ✅ constructor injection (çalışanları bozmaz)
    public ProductService(ProductRepository productRepository,
                          ProductTranslationRepository translationRepository) {
        this.productRepository = productRepository;
        this.translationRepository = translationRepository;
    }

    // ✅ i18n: istenen dilde translation seç
    private ProductTranslation pickTranslation(Product p, String lang) {
        if (p == null || p.getTranslations() == null || p.getTranslations().isEmpty()) return null;

        String l = (lang == null || lang.isBlank()) ? "tr" : lang.toLowerCase(Locale.ROOT);

        // 1) tam eşleşme
        for (ProductTranslation t : p.getTranslations()) {
            if (t != null && t.getLang() != null && t.getLang().equalsIgnoreCase(l)) return t;
        }
        // 2) fallback tr
        for (ProductTranslation t : p.getTranslations()) {
            if (t != null && "tr".equalsIgnoreCase(t.getLang())) return t;
        }
        // 3) ilk
        return p.getTranslations().get(0);
    }

    // ✅ Android için DTO üret (entity dönmeyelim)
    public Map<String, Object> toI18nDto(Product p, String lang) {
        ProductTranslation t = pickTranslation(p, lang);

        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", p.getId());
        dto.put("price", p.getPrice());
        dto.put("img", p.getImg());
        dto.put("labels", p.getLabels());
        dto.put("brand", p.getBrand());
        dto.put("color", p.getColor());
        dto.put("categoryKey", p.getCategoryKey());

        dto.put("title", t != null ? t.getTitle() : null);
        dto.put("description", t != null ? t.getDescription() : null);
        dto.put("tags", t != null ? t.getTags() : null);
        dto.put("searchText", t != null ? t.getSearchText() : null);
        dto.put("material", t != null ? t.getMaterial() : null);
        dto.put("productType", t != null ? t.getProductType() : null);
        dto.put("category", t != null ? t.getCategoryName() : null);

        return dto;
    }

    /* ------------------ ✅ NEW: Translation Upsert ------------------ */

    @Transactional
    public Map<String, Object> upsertTranslation(Long productId, ProductTranslationRequest req) {
        if (req == null) throw new RuntimeException("Translation body is required");

        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        String lang = (req.lang == null || req.lang.isBlank())
                ? "tr"
                : req.lang.toLowerCase(Locale.ROOT);

        ProductTranslation t = translationRepository
                .findByProductIdAndLang(productId, lang)
                .orElseGet(ProductTranslation::new);

        t.setProduct(p);
        t.setLang(lang);

        t.setTitle(req.title);
        t.setDescription(req.description);
        t.setTags(req.tags);

        String st = req.searchText;
        if (st == null || st.isBlank()) {
            String a = req.title == null ? "" : req.title;
            String b = req.description == null ? "" : req.description;
            String c = req.tags == null ? "" : req.tags;
            st = (a + " " + b + " " + c).trim();
        }
        t.setSearchText(st);

        t.setMaterial(req.material);
        t.setProductType(req.productType);
        t.setCategoryName(req.categoryName);

        ProductTranslation savedT = translationRepository.save(t);

        // UI/React için küçük dönüş
        return Map.of(
                "ok", true,
                "productId", p.getId(),
                "translationId", savedT.getId(),
                "lang", savedT.getLang()
        );
    }

    // ------------------ MEVCUT METODLAR (korundu) ------------------

    public ResponseEntity<Product> getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found in DB"));
        return ResponseEntity.ok(product);
    }

    public ResponseEntity<List<Product>> allProducts() {
        List<Product> productList = productRepository.findAll();
        return ResponseEntity.ok(productList);
    }

    public ResponseEntity<Product> createProduct(Product product) {
        return ResponseEntity.ok().body(productRepository.save(product));
    }

    public ResponseEntity<Product> updateProduct(Long productId, Product updatedProduct) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found in DB"));

        // ⚠️ Yeni şemada title/category/description product'ta yok.
        // Sadece mevcut alanları güncelliyoruz:
        product.setImg(updatedProduct.getImg());
        product.setPrice(updatedProduct.getPrice());
        product.setLabels(updatedProduct.getLabels());
        product.setBrand(updatedProduct.getBrand());
        product.setColor(updatedProduct.getColor());
        product.setCategoryKey(updatedProduct.getCategoryKey());

        productRepository.save(product);
        return ResponseEntity.ok(product);
    }

    public ResponseEntity<String> deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return ResponseEntity.ok("Product deleted successfully");
        } else {
            throw new RuntimeException("Product not found in DB");
        }
    }

    public ResponseEntity<String> deleteAllProducts() {
        productRepository.deleteAll();
        return ResponseEntity.ok("All products deleted successfully");
    }

    // --- resim upload (korundu) ---
    public Product uploadImage(Long id, MultipartFile file) throws Exception {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filepath = Paths.get("./images/products/", filename);
        Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

        product.setImg(filename);
        return productRepository.save(product);
    }

    public Page<Product> getPaged(int page, int size) {
        return productRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))
        );
    }

    @Transactional
    public void handleCategoryKeyChange(String oldKey, String newKey) {
        try {
            if (oldKey == null || newKey == null || oldKey.equalsIgnoreCase(newKey)) {
                log.debug("Category key update ignored (null or same): {} -> {}", oldKey, newKey);
                return;
            }
            int updatedCount = productRepository.updateCategoryKeyForProducts(oldKey, newKey);
            log.info("Updated {} products: categoryKey '{}' -> '{}'", updatedCount, oldKey, newKey);
        } catch (Exception ex) {
            log.error("Failed to update product categoryKeys for '{}' -> '{}': {}", oldKey, newKey, ex.getMessage(), ex);
        }
    }


    public List<Map<String, Object>> chatAiSearchI18n(String query, String lang, int topK) {
        String q = (query == null) ? "" : query.trim();
        String l = (lang == null || lang.isBlank()) ? "tr" : lang.toLowerCase(Locale.ROOT);

        List<Product> found;
        if (q.isBlank()) {
            found = productRepository.findAll(PageRequest.of(0, topK)).getContent();
        } else {
            found = productRepository.searchI18n(l, q, PageRequest.of(0, topK));
        }

        return found.stream()
                .map(p -> toI18nDto(p, l))
                .toList();
    }
}