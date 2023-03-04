package org.example.usersservice.service;

import lombok.AllArgsConstructor;
import org.example.usersservice.model.AppUser;
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

    public void updateNonMandatoryFieldsByUserId(Long userId, String firstname, String lastname, String phoneNumber) {
        usersRepository.updateNonMandatoryFieldsById(userId, firstname, lastname, phoneNumber);
    }
}
