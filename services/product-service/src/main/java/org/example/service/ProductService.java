package org.example.service;

import org.example.dto.CreateProductDTO;
import org.example.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductDto createProduct(CreateProductDTO dto);
    ProductDto getProductById(Long productId);
    Page<ProductDto> getAllProductsByUserId(Long userId, Pageable pageable);
    void delete(Long productId);

}
