package org.example.apigateway.client;

import org.example.apigateway.client.dto.SellerDto;
import org.example.apigateway.dto.seller.CreateSellerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(name = "seller-service", url = "${services.auction-service.url}/api/v1/sellers")
public interface SellerClient {
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    SellerDto createNewSeller(CreateSellerDto sellerDto);

    @RequestMapping(value = "/{email}", method = RequestMethod.GET)
    SellerDto findSellerIdByEmail(@PathVariable("email") String sellerEmail);
}
