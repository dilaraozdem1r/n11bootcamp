package com.n11bootcamp.product_service.controller;

import com.n11bootcamp.product_service.dto.ProductTranslationRequest;
import com.n11bootcamp.product_service.entity.Product;
import com.n11bootcamp.product_service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8081/", "http://localhost:4200",
        "http://85.159.71.66:8081/", "http://85.159.71.66:3000",
        "http://94.73.134.50:4200/", "http://94.73.134.50:4200",
        "http://94.73.134.50:8081/", "http://94.73.134.50:3000"})
@RequestMapping("api/product")

public class ProductController {

    @Autowired
    private ProductService productService;




    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }



    @PutMapping("{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") Long productId,
                                                 @RequestBody Product updatedProduct) {
        return productService.updateProduct(productId, updatedProduct);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteById(@PathVariable("id") Long id) {
        return productService.deleteProduct(id);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAll() {
        return productService.deleteAllProducts();
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return productService.allProducts();
    }



    /**
     * ✅ MCP / Search Dialog burayı kullanıyor.
     * ✅ Accept-Language'a göre (tr/en/de/fr) translation içinde arar ve localized DTO döner.
     */
    @PostMapping("/chat-ai")
    public ResponseEntity<?> chatWithAi(
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "Accept-Language", required = false) String lang
    ) {
        Object qObj = payload.get("query");
        String query = qObj != null ? String.valueOf(qObj).trim() : "";

        Integer topK = null;
        Object topKObj = payload.get("topK");
        if (topKObj instanceof Number n) topK = n.intValue();
        if (topK == null || topK <= 0) topK = 20;

        return ResponseEntity.ok(productService.chatAiSearchI18n(query, lang, topK));
    }

    @GetMapping("/paged")
    public ResponseEntity<?> getPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size
    ) {
        Page<Product> p = productService.getPaged(page, size);
        return ResponseEntity.ok(Map.of(
                "items", p.getContent(),
                "page", p.getNumber(),
                "size", p.getSize(),
                "totalElements", p.getTotalElements(),
                "totalPages", p.getTotalPages(),
                "isLast", p.isLast()
        ));
    }

    @GetMapping("{id}")
    public ResponseEntity<Product> getProductById(
            @PathVariable("id") Long productId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestHeader(value = "X-Platform", required = false, defaultValue = "WEB") String platform,
            @RequestHeader(value = "X-Source", required = false, defaultValue = "REACT") String source,
            @RequestHeader(value = "X-Session-Id", required = false, defaultValue = "unknown") String sessionId
    ) {
        ResponseEntity<Product> response = productService.getProductById(productId);



        return response;
    }

    // ------------------ ✅ i18n DTO endpoint'ler (korundu) ------------------

    @GetMapping("/i18n")
    public ResponseEntity<List<Map<String, Object>>> getAllI18n(
            @RequestHeader(value = "Accept-Language", required = false) String lang
    ) {
        List<Product> list = productService.allProducts().getBody();
        if (list == null) list = List.of();

        List<Map<String, Object>> dto = list.stream()
                .map(p -> productService.toI18nDto(p, lang))
                .toList();

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/i18n/{id}")
    public ResponseEntity<Map<String, Object>> getI18nById(
            @PathVariable("id") Long id,
            @RequestHeader(value = "Accept-Language", required = false) String lang
    ) {
        Product p = productService.getProductById(id).getBody();
        if (p == null) throw new RuntimeException("Product not found");
        return ResponseEntity.ok(productService.toI18nDto(p, lang));
    }

    @GetMapping("/i18n/paged")
    public ResponseEntity<?> getPagedI18n(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size,
            @RequestHeader(value = "Accept-Language", required = false) String lang
    ) {
        Page<Product> p = productService.getPaged(page, size);

        List<Map<String, Object>> items = p.getContent().stream()
                .map(prod -> productService.toI18nDto(prod, lang))
                .toList();

        return ResponseEntity.ok(Map.of(
                "items", items,
                "page", p.getNumber(),
                "size", p.getSize(),
                "totalElements", p.getTotalElements(),
                "totalPages", p.getTotalPages(),
                "isLast", p.isLast()
        ));
    }

    /* ------------------ ✅ NEW: Translation Upsert Endpoint ------------------ */

    @PostMapping("/{id}/translations")
    public ResponseEntity<?> upsertTranslation(
            @PathVariable("id") Long id,
            @RequestBody ProductTranslationRequest req
    ) {
        return ResponseEntity.ok(productService.upsertTranslation(id, req));
    }
}