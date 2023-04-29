package org.example.apigateway.service;

import lombok.AllArgsConstructor;
import org.example.apigateway.client.BuyerClient;
import org.example.apigateway.dto.buyer.CreateBuyerWithUserEmailDto;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BuyerService {
    private final BuyerClient buyerClient;

    public void createBuyer(CreateBuyerWithUserEmailDto createBuyerWithUserEmailDto) {
        buyerClient.createBuyer(createBuyerWithUserEmailDto);
    }
}
