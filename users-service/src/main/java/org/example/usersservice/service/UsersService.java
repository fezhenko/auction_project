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

}
