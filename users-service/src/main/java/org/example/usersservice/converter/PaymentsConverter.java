package org.example.usersservice.converter;

import java.util.List;

import jakarta.validation.Valid;
import org.example.usersservice.dto.payments.PaymentDto;
import org.example.usersservice.model.Payment;
import org.mapstruct.Mapper;

@Mapper
public interface PaymentsConverter {
    List<PaymentDto> toDto(@Valid List<Payment> paymentList);

    PaymentDto toDto(@Valid Payment payment);
}
