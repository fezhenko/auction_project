package org.example.auction.scheduler.client;

import jakarta.validation.Valid;
import org.example.auction.scheduler.client.dto.AppUserDto;
import org.example.auction.scheduler.client.dto.FinalPriceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "users-service", url = "${services.users-service.url}/api/v1/users")
public interface UserClient {
    @GetMapping("/validate")
    AppUserDto findUserByEmail(@RequestParam(value = "email") String email);

    @PatchMapping(
            value = "/{userId}/balance",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    void updateUserBalance(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "userType") String userType,
            @RequestBody @Valid FinalPriceDto finalPrice);
}
