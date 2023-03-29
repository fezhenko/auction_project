package org.example.apigateway.controller;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.apigateway.dto.bid.CreateBidDto;
import org.example.apigateway.dto.bid.MakeBidResultDto;
import org.example.apigateway.dto.buyer.CreateBuyerDto;
import org.example.apigateway.dto.buyer.CreateBuyerResultDto;
import org.example.apigateway.dto.buyer.CreateBuyerWithUserEmailDto;
import org.example.apigateway.service.BidService;
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

    private final BidService bidService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private ResponseEntity<CreateBuyerResultDto> enrollToAuction(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                                                 @RequestBody @Valid CreateBuyerDto createBuyerDto) {
        if (token.isEmpty()) {
            CreateBuyerResultDto result = CreateBuyerResultDto.builder().message("Invalid token").build();
            return ResponseEntity.badRequest().body(result);
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
    }

    @PostMapping("/bids")
    @ResponseStatus(HttpStatus.ACCEPTED)
    private ResponseEntity<MakeBidResultDto> makeBidToAuction(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                                              @RequestBody @Valid CreateBidDto createBidDto) {
        if (token.isEmpty()) {
            MakeBidResultDto result = MakeBidResultDto.builder().message("Invalid token").build();
            return ResponseEntity.badRequest().body(result);
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getUsername() == null) {
            MakeBidResultDto result = MakeBidResultDto.builder()
                    .message("User with '%s' is not exist".formatted(user.getUsername())).build();
            return ResponseEntity.badRequest().body(result);
        }
        MakeBidResultDto makeBidResultDto = bidService.makeBidToAuction(user.getUsername(), createBidDto.getAmount());
        if (makeBidResultDto.getMessage() == null) {
            return ResponseEntity.accepted().build();
        }
        return ResponseEntity.badRequest().body(makeBidResultDto);
    }

}
//TODO: юзер выйграл аукцион
// если файнал прайс не равен нулю, получаю айди байера, и сумму равную файнал прайсу
// через байер айди нахожу юзера, отнимаю сумму ставки из баланса, если не достаточно, нужно закенселить аукцион.
