package org.example.usersservice.converter;

import org.example.usersservice.dto.payments.PaymentDto;
import org.example.usersservice.dto.users.AppUserDto;
import org.example.usersservice.model.Payment;
import org.mapstruct.Mapper;

import javax.validation.Valid;
import java.util.List;

@Mapper
public interface PaymentsConverter {
    List<PaymentDto> toDto(@Valid List<Payment> paymentList);

    AppUserDto toDto(@Valid Payment payment);
}
