package org.example.usersservice;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.usersservice.controller.UsersController;
import org.example.usersservice.converter.UsersConverter;
import org.example.usersservice.dto.CreateUserDto;
import org.example.usersservice.model.AppUser;
import org.example.usersservice.service.UsersService;
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
import java.util.Date;
import java.util.List;


@ExtendWith(SpringExtension.class)
@WebMvcTest({UsersController.class, UsersConverter.class})
@Slf4j
public class UsersControllerTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersService usersService;

    @MockBean
    private UsersConverter usersConverter;

    @SneakyThrows
    @Test
    @DisplayName("Successfully find users")
    public void findUsersTest() {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isOk());
        BDDMockito.then(usersService)
                .should()
                .findUsers();
        final List<AppUser> users = new ArrayList<>();
        BDDMockito.then(usersConverter)
                .should()
                .toDto(users);
    }

    @SneakyThrows
    @Test
    @DisplayName("User should be found by user id")
    public void userIsFoundById() {
        final Long userId = 123123L;
        final AppUser appUser = new AppUser(
                123123L,
                "firstName",
                "lastName",
                "email@test.test",
                "password1",
                "USER",
                0.0,
                "1234567890",
                new Date(3000)
        );
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isOk());

        BDDMockito.then(usersService)
                .should()
                .getUserById(userId);
    }

    @Test
    @SneakyThrows
    public void testUserCreationUsingValidData() {
        final CreateUserDto createUserWithValidData = CreateUserDto.builder()
                .email("test@admin.test")
                .password("myPassword1")
                .role("ADMIN")
                .build();
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(createUserWithValidData))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isCreated());
        BDDMockito.then(usersService)
                .should()
                .createUser(createUserWithValidData.getEmail(),
                        createUserWithValidData.getPassword(),
                        createUserWithValidData.getRole());
    }

}
