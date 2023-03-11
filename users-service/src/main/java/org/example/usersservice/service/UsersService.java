package org.example.usersservice.service;

import lombok.AllArgsConstructor;
import org.example.usersservice.model.AppUser;
import org.example.usersservice.model.Payment;
import org.example.usersservice.repository.UsersRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;

    public List<AppUser> findUsers() {
        return usersRepository.findUsers();
    }

    public AppUser getUserById(Long userId) {
        return usersRepository.getAppUserById(userId);
    }

    public void createUser(String email, String password, String role) {
        usersRepository.createUser(email, password, role);
    }

    public void updateFieldsByUserId(
            Long userId,
            String email,
            String firstname,
            String lastname,
            String phoneNumber) {
        usersRepository.updateFieldsByUserId(userId, email, firstname, lastname, phoneNumber);
    }

    public void deleteUserById(Long userId) {
        usersRepository.deleteAppUserById(userId);
    }

    public boolean validateUserByEmail(String email) {
        return usersRepository.validateUser(email);
    }

    public void updateUserPassword(Long userId, String password) {
        usersRepository.updateUserPasswordById(userId, password);
    }

    public List<Payment> findPaymentsByUserId(Long userId) {
        return usersRepository.findPaymentsByUserId(userId);
    }

    public Payment updatePaymentInformation(Long userId, Long paymentId, String cardNumber, String expirationDate) {
        return usersRepository.updatePaymentInformationByUserId(
                userId,
                paymentId,
                cardNumber,
                expirationDate
        );
    }

}
