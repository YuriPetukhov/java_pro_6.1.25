package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.factory.ProductFactory;
import org.example.dto.CreateProductDTO;
import org.example.dto.ProductDto;
import org.example.entity.Product;
import org.example.entity.User;
import org.example.exception.NotFoundException;
import org.example.mapper.ProductMapper;
import org.example.repository.ProductRepository;
import org.example.service.ProductService;
import org.example.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация сервиса управления продуктами.
 * Инкапсулирует бизнес-логику создания и чтения продуктов,
 * а также взаимодействие с репозиториями и конвертерами.
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserService userService;
    private final ProductFactory productFactory;
    private final ProductMapper productMapper;

    /**
     * Создаёт новый продукт для указанного пользователя.
     * Если пользователь не найден, выбрасывается {@link NotFoundException}.
     *
     * @param dto входные данные продукта
     * @return DTO созданного продукта
     * @throws NotFoundException если пользователь с {@code dto.userId()} не найден
     */
    @Override
    @Transactional
    public ProductDto createProduct(CreateProductDTO dto) {
        User user = userService.findById(dto.userId())
                .orElseThrow(() -> new NotFoundException("User not found: " + dto.userId()));

        Product product = productFactory.from(dto, user);
        product = productRepository.save(product);

        return productMapper.toDto(product);
    }

    /**
     * Возвращает продукт по идентификатору.
     *
     * @param productId идентификатор продукта
     * @return DTO продукта
     * @throws NotFoundException если продукт не найден
     */
    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(Long productId) {
        return productRepository.findById(productId)
                .map(productMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Product not found: " + productId));
    }

    /**
     * Возвращает постраничный список продуктов пользователя.
     * Предварительно проверяет наличие пользователя.
     *
     * @param userId   идентификатор пользователя
     * @param pageable параметры пагинации
     * @return страница DTO продуктов
     * @throws NotFoundException если пользователь не найден
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProductsByUserId(Long userId, Pageable pageable) {
        if (!userService.existsById(userId)) {
            throw new NotFoundException("User not found: " + userId);
        }
        return productRepository.findAllByUserId(userId, pageable)
                .map(productMapper::toDto);
    }

    /**
     * Удаляет продукт по идентификатору.
     * <p>
     * Операция идемпотентна: если продукта с указанным идентификатором нет,
     * метод не выбрасывает исключение и завершает выполнение без ошибок.
     *
     * @param productId идентификатор продукта
     */
    @Override
    @Transactional
    public void delete(Long productId) {
        try {
            productRepository.deleteById(productId);
        } catch (org.springframework.dao.EmptyResultDataAccessException ignored) {
        }
    }
}
