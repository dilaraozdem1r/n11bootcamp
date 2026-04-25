package com.n11bootcamp.product_service.repository;

import com.n11bootcamp.product_service.entity.ProductTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductTranslationRepository extends JpaRepository<ProductTranslation, Long> {
    Optional<ProductTranslation> findByProductIdAndLang(Long productId, String lang);

}
