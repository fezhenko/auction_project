package org.example.apigateway.service;

import lombok.AllArgsConstructor;
import org.example.apigateway.client.BuyerClient;
import org.example.apigateway.dto.buyer.CreateBuyerResultDto;
import org.example.apigateway.dto.buyer.CreateBuyerWithUserEmailDto;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BuyerService {
    private final BuyerClient buyerClient;

    public CreateBuyerResultDto createBuyer(CreateBuyerWithUserEmailDto createBuyerWithUserEmailDto) {
        if (createBuyerWithUserEmailDto.getUserEmail() == null) {
            CreateBuyerResultDto.builder().message("user email cannot be null").build();
        }
        buyerClient.createBuyer(createBuyerWithUserEmailDto);
        return CreateBuyerResultDto.builder().build();
    }
}
