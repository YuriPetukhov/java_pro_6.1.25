package org.example.service;

import org.example.dto.PageResponse;
import org.example.dto.PaymentRequest;
import org.example.dto.PaymentResponse;
import org.example.dto.ProductDto;

import java.util.List;

public interface PaymentService {
    PageResponse<ProductDto> fetchUserProducts(Long userId, Integer page, Integer size, String sort);

    PaymentResponse execute(PaymentRequest req);
}
