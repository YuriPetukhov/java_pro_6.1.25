package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.CreateProductDTO;
import org.example.dto.PageResponse;
import org.example.dto.ProductDto;
import org.example.mapper.PageMappers;
import org.example.service.ProductService;
import org.springdoc.core.annotations.ParameterObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@Tag(name = "Products API", description = "Создание и получение продуктов пользователей")
public class ProductController {

    private final ProductService productService;
    private final PageMappers pageMappers;

    /**
     * Создает новый продукт.
     * В заголовке Location указывается ссылка на созданный ресурс.
     *
     * @param newProduct DTO с данными для создания продукта
     * @return созданный продукт
     */
    @PostMapping
    @Operation(
            summary = "Создать продукт",
            description = "Создает продукт для указанного пользователя и возвращает его представление. " +
                    "Заголовок Location указывает на URL созданного ресурса."
    )
    @ApiResponse(responseCode = "201", description = "Продукт создан")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody CreateProductDTO newProduct) {
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
     * @param pageable параметры пагинации/сортировки (page, size, sort=field,asc|desc)
     * @return страница DTO продуктов в стабильном формате PageResponse
     */
    @GetMapping(params = "userId")
    @Operation(
            summary = "Получить продукты пользователя (с пагинацией)",
            description = "Возвращает постраничный список продуктов указанного пользователя. " +
                    "Поддерживаются параметры page, size и sort (например, sort=balance,desc)."
    )
    @ApiResponse(responseCode = "200", description = "Страница продуктов получена")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    public PageResponse<ProductDto> getAllProductsByUserId(
            @Parameter(description = "Идентификатор пользователя") @RequestParam Long userId,
            @ParameterObject Pageable pageable
    ) {
        return pageMappers.toResponse(productService.getAllProductsByUserId(userId, pageable));
    }

    /**
     * Возвращает продукт по его идентификатору.
     *
     * @param id идентификатор продукта
     * @return DTO продукта
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Получить продукт по ID",
            description = "Возвращает продукт по его идентификатору."
    )
    @ApiResponse(responseCode = "200", description = "Продукт найден")
    @ApiResponse(responseCode = "404", description = "Продукт не найден")
    public ProductDto getProductById(
            @Parameter(description = "Идентификатор продукта") @PathVariable Long id
    ) {
        return productService.getProductById(id);
    }
}
