package org.example.apigateway.client;

import org.example.apigateway.client.dto.SellerDto;
import org.example.apigateway.dto.seller.CreateSellerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "seller-service", url = "${services.auction-service.url}/api/v1/sellers")
public interface SellerClient {
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    SellerDto createNewSeller(CreateSellerDto sellerDto);

    @GetMapping("/{email}")
    SellerDto findSellerIdByEmail(@PathVariable("email") String sellerEmail);
}
