package org.example.usersservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.usersservice.converter.PaymentsConverter;
import org.example.usersservice.dto.payments.PaymentDto;
import org.example.usersservice.model.Payment;
import org.example.usersservice.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments", description = "api to work with payments")
@AllArgsConstructor
public class PaymentsController {
    private final PaymentService paymentService;
    private final PaymentsConverter paymentConverter;

    @Tag(name = "Payments")
    @Operation(summary = "Get all payments from database")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User password is updated")
            }
    )
    @GetMapping()
    public ResponseEntity<List<PaymentDto>> findAllPayments() {
        List<Payment> payments = paymentService.findPayments();
        return ResponseEntity.ok(paymentConverter.toDto(payments));
    }

}
