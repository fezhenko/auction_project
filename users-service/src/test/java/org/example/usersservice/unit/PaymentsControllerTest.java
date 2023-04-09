package org.example.usersservice.unit;

import lombok.SneakyThrows;
import org.example.usersservice.controller.PaymentsController;
import org.example.usersservice.converter.PaymentsConverter;
import org.example.usersservice.dto.payments.CreatePaymentDto;
import org.example.usersservice.model.Payment;
import org.example.usersservice.service.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PaymentsController.class)
public class PaymentsControllerTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;
    @MockBean
    private PaymentsConverter paymentsConverter;

    @Test
    @DisplayName("verify that payment service is called")
    @SneakyThrows
    public void testFindAllPayments() {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/payments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isOk());
        BDDMockito.then(paymentService)
                .should()
                .findPayments();
        final List<Payment> users = new ArrayList<>();
        BDDMockito.then(paymentsConverter)
                .should()
                .toDto(users);
    }

    @Test
    @DisplayName("verify that payment calls payment service")
    @SneakyThrows
    public void testCreatingNewPayment() {
        CreatePaymentDto payment = CreatePaymentDto.builder()
                .userId(123123L)
                .cardNumber("0000111122223333")
                .expirationDate("02/22")
                .amount(11.99d)
                .build();
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(payment))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isCreated());
        BDDMockito.then(paymentService)
                .should()
                .createPayment(
                        payment.getUserId(),
                        payment.getCardNumber(),
                        payment.getExpirationDate(),
                        payment.getAmount()
            );
    }

}
