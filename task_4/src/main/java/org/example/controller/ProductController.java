package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.CreateProductDTO;
import org.example.dto.ProductDto;
import org.example.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * REST-контроллер для управления продуктами.
 * Предоставляет методы создания и получения продуктов.
 */
@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Создает новый продукт.
     * В заголовке Location указывается ссылка на созданный ресурс.
     *
     * @param newProduct DTO с данными для создания продукта
     * @return ResponseEntity с данными созданного продукта и заголовком Location
     */
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
            @Valid @RequestBody CreateProductDTO newProduct) {
        ProductDto dto = productService.createProduct(newProduct);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.id())
                .toUri();

        return ResponseEntity.created(location).body(dto);
    }

    /**
     * Возвращает страницу продуктов, принадлежащих конкретному пользователю.
     *
     * @param userId   идентификатор пользователя
     * @param pageable параметры постраничного вывода
     * @return страница DTO продуктов
     */
    @GetMapping(params = "userId")
    public Page<ProductDto> getAllProductsByUserId(@RequestParam Long userId, Pageable pageable) {
        return productService.getAllProductsByUserId(userId, pageable);
    }

    /**
     * Возвращает продукт по его идентификатору.
     *
     * @param id идентификатор продукта
     * @return DTO продукта
     */
    @GetMapping("/{id}")
    public ProductDto getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }
}



