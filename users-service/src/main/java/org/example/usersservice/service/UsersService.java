package org.example.usersservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.usersservice.dto.users.UpdateBalanceResultDto;
import org.example.usersservice.dto.users.UserBalanceDto;
import org.example.usersservice.dto.users.UserVerificationDto;
import org.example.usersservice.exceptions.UserBalanceIsLessThanPriceException;
import org.example.usersservice.exceptions.UserIdIsNullException;
import org.example.usersservice.model.AppUser;
import org.example.usersservice.model.Payment;
import org.example.usersservice.repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
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
            Long userId, String email, String firstname, String lastname, String phoneNumber
    ) {
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
        return usersRepository.updatePaymentInformationByUserId(userId, paymentId, cardNumber, expirationDate);
    }

    public UserVerificationDto verifyUser(String email) {
        AppUser user = usersRepository.findUserByEmail(email);
        if (user == null) {
            return UserVerificationDto.builder().build();
        }
        return UserVerificationDto.builder().email(user.getEmail()).password(user.getPassword()).build();
    }

    public AppUser findUserByEmail(String email) {
        return usersRepository.findUserByEmail(email);
    }

    public UserBalanceDto getUserBalanceById(Long userId) {
        AppUser user = getUserById(userId);
        if (user == null) {
            return UserBalanceDto.builder().build();
        }
        return UserBalanceDto.builder().balance(usersRepository.getCurrentUserBalance(userId)).build();
    }

    @Transactional(readOnly = true)
    public UpdateBalanceResultDto updateUserBalance(Long id, String userType, Double price) {
        if (id == null) {
            log.error("user id should be not null to update balance");
            throw new UserIdIsNullException("user id should be not null to update balance");
        }
        Double userBalance = usersRepository.findBalanceByUserId(id);
        double newBalance = 0.0000;
        switch (userType) {
            case "seller": {
                newBalance = userBalance + price;
                usersRepository.updateUserBalanceAfterAuctionFinish(id, newBalance);
                break;
            }
            case "buyer": {
                if (price > userBalance) {
                    log.error("user id:'%d' balance is less than price".formatted(id));
                    throw new UserBalanceIsLessThanPriceException("user id:'%d' balance is less than price".formatted(id));
                }
                newBalance = userBalance - price;
                usersRepository.updateUserBalanceAfterAuctionFinish(id, newBalance);
                break;
            }
        }
        return UpdateBalanceResultDto.builder().balance(newBalance).build();
    }
}
