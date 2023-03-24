package org.example.apigateway.controller;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.apigateway.dto.buyer.CreateBuyerDto;
import org.example.apigateway.dto.buyer.CreateBuyerResultDto;
import org.example.apigateway.dto.buyer.CreateBuyerWithUserEmailDto;
import org.example.apigateway.service.BuyerService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/buyers")
@AllArgsConstructor
@SecurityScheme(type = SecuritySchemeType.HTTP, bearerFormat = "JWT")
@Slf4j
public class BuyerController {

    private final BuyerService buyerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private ResponseEntity<CreateBuyerResultDto> enrollToAuction(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                                                 @RequestBody @Valid CreateBuyerDto createBuyerDto) {
        if (token.isEmpty()) {
            CreateBuyerResultDto result = CreateBuyerResultDto.builder().message("Invalid token").build();
            return ResponseEntity.badRequest().body(result);
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // при создании байера, беру юзернейм из контекста, сохраняю его в таблице байеров
        CreateBuyerResultDto result = buyerService.createBuyer(
                CreateBuyerWithUserEmailDto.builder()
                        .userEmail(user.getUsername())
                        .auctionId(createBuyerDto.getAuctionId())
                        .build()
        );

        if (result.getMessage() == null) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.badRequest().body(result);

        // юзер выйграл аукцион
        // каррент прайс становится файнал прайсом
        // если файнал прайс не равен нулю, получаю айди байера, и сумму равную файнал прайсу
        // через байер айди нахожу юзера, отнимаю сумму ставки из баланса, если не достаточно, нужно закенселить аукцион.
    }
}
