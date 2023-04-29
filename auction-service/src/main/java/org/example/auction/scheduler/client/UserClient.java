package org.example.auction.scheduler.client;


import javax.validation.Valid;
import org.example.auction.scheduler.client.dto.AppUserDto;
import org.example.auction.scheduler.client.dto.FinalPriceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "users-service", url = "${services.users-service.url}/api/v1/users")
public interface UserClient {
    @RequestMapping(method = RequestMethod.GET, value = "/validate")
    AppUserDto findUserByEmail(@RequestParam(value = "email") String email);

    @RequestMapping(method = RequestMethod.PATCH, value = "/{userId}/balance",
        consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    void updateUserBalance(@PathVariable("userId") Long userId,
                           @RequestParam(value = "userType") String userType,
                           @RequestBody @Valid FinalPriceDto finalPrice);
}
