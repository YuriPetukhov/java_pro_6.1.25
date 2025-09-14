package org.example.bootstrap;

import lombok.RequiredArgsConstructor;
import org.example.dto.CreateProductDTO;
import org.example.dto.ProductDto;
import org.example.entity.User;
import org.example.enums.ProductType;
import org.example.service.ProductService;
import org.example.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile("demo")
@RequiredArgsConstructor
public class DemoDataRunner implements CommandLineRunner {

    private final UserService userService;
    private final ProductService productService;

    @Override
    public void run(String... args) {
        System.out.println("\n=== DEMO START ===");

        User alice = userService.createUser("alice");
        User bob   = userService.createUser("bob");
        System.out.println("Users: " + userService.getAll());

        productService.createProduct(new CreateProductDTO(
                alice.getId(), "AL-001", new BigDecimal("500.00"), ProductType.ACCOUNT));
        productService.createProduct(new CreateProductDTO(
                alice.getId(), "AL-002", new BigDecimal("1500.00"), ProductType.CARD));
        productService.createProduct(new CreateProductDTO(
                bob.getId(),   "BO-001", new BigDecimal("200.00"), ProductType.ACCOUNT));

        Pageable pageReq = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        Page<ProductDto> aliceProducts = productService.getAllProductsByUserId(alice.getId(), pageReq);
        Page<ProductDto> bobProducts   = productService.getAllProductsByUserId(bob.getId(), pageReq);

        System.out.println("\n-- Products of alice --");
        aliceProducts.forEach(System.out::println);

        System.out.println("\n-- Products of bob --");
        bobProducts.forEach(System.out::println);

        System.out.println("\n=== DEMO END ===\n");
    }
}
