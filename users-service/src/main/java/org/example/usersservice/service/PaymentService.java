package org.example.usersservice.service;

import lombok.AllArgsConstructor;
import org.example.usersservice.model.Payment;
import org.example.usersservice.repository.PaymentsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentsRepository paymentsRepository;

    public List<Payment> findPayments() {
        return paymentsRepository.findAllPayments();
    }
}
