package org.example.usersservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.usersservice.converter.PaymentsConverter;
import org.example.usersservice.dto.payments.CreatePaymentDto;
import org.example.usersservice.dto.payments.PaymentDto;
import org.example.usersservice.model.Payment;
import org.example.usersservice.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
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
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of available payments")})
    @GetMapping()
    public ResponseEntity<List<PaymentDto>> findAllPayments() {
        List<Payment> payments = paymentService.findPayments();
        return ResponseEntity.ok(paymentConverter.toDto(payments));
    }

    @Tag(name = "Payments")
    @Operation(summary = "Get payment information by userId")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "User password is updated")})
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDto> findPaymentById(@PathVariable Long paymentId) {
        Payment payment = paymentService.findPayment(paymentId);
        return ResponseEntity.ok(paymentConverter.toDto(payment));
    }

    @PostMapping()
    public void addNewPayment(@Valid @RequestBody CreatePaymentDto createPaymentDto) {
        paymentService.createPayment(
                createPaymentDto.getUserId(),
                createPaymentDto.getCardNumber(),
                createPaymentDto.getExpirationDate(),
                createPaymentDto.getAmount());
    }

}
