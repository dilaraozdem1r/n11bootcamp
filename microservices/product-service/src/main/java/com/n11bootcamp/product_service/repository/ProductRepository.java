package com.n11bootcamp.product_service.repository;


import com.n11bootcamp.product_service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"translations"})
    Optional<Product> findById(Long id);

    @EntityGraph(attributePaths = {"translations"})
    List<Product> findAll();

    @EntityGraph(attributePaths = {"translations"})
    Page<Product> findAll(Pageable pageable);

    @Modifying
    @Query(value = "UPDATE public.product SET category_key = :newKey WHERE LOWER(TRIM(category_key)) = LOWER(TRIM(:oldKey))", nativeQuery = true)
    int updateCategoryKeyForProducts(@Param("oldKey") String oldKey, @Param("newKey") String newKey);

    @EntityGraph(attributePaths = {"translations"})
    @Query("""
    select distinct p
    from Product p
    join p.translations t
    where lower(t.lang) = lower(:lang)
      and (
        lower(coalesce(t.searchText,'')) like lower(concat('%', :q, '%'))
        or lower(coalesce(t.title,'')) like lower(concat('%', :q, '%'))
        or lower(coalesce(t.description,'')) like lower(concat('%', :q, '%'))
        or lower(coalesce(t.tags,'')) like lower(concat('%', :q, '%'))
      )
    order by p.id desc
""")
    List<Product> searchI18n(@Param("lang") String lang, @Param("q") String q, Pageable pageable);

}