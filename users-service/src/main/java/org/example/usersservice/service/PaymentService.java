package org.example.usersservice.service;

import lombok.AllArgsConstructor;
import org.example.usersservice.model.Payment;
import org.example.usersservice.repository.PaymentsRepository;
import org.example.usersservice.repository.UsersRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentsRepository paymentsRepository;
    private final UsersRepository usersRepository;

    public List<Payment> findPayments() {
        return paymentsRepository.findAllPayments();
    }

    public Payment findPayment(Long paymentId) {
        return paymentsRepository.findPaymentsByPaymentId(paymentId);
    }

    public void createPayment(Long userId, String cardNumber, String expirationDate, Double amount) {
        paymentsRepository.addPaymentByUserId(userId, cardNumber, expirationDate, amount);
        Double currentBalance = usersRepository.getCurrentUserBalance(userId);
        usersRepository.updateUserBalanceWithNewPayment(userId, currentBalance + amount);
    }

    public void updatePaymentInformation(Long paymentId, String cardNumber, String expirationDate) {
        paymentsRepository.updatePaymentInformationById(paymentId, cardNumber, expirationDate);
    }

}
