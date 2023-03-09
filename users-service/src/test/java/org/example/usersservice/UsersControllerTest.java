package org.example.usersservice;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.usersservice.controller.UsersController;
import org.example.usersservice.converter.UsersConverter;
import org.example.usersservice.dto.users.CreateUserDto;
import org.example.usersservice.dto.users.UpdateUserRelatedFieldsDto;
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
    public void testUserCanBeFoundByValidUserId() {
        final Long userId = 123123L;

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

    @Test
    @SneakyThrows
    @DisplayName("verify that user is deleted if valid user Id is provided")
    public void testUserDeletingWithValidUserId() {
        final Long userId = 123L;
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isAccepted());
        BDDMockito.then(usersService)
                .should()
                .deleteUserById(userId);
    }

    @Test
    @DisplayName("Verify that user service will be called when put endpoint is called")
    @SneakyThrows
    public void testFieldsUpdatingWithValidUserId() {
        final Long userId = 123L;
        final UpdateUserRelatedFieldsDto fieldsToUpdate = UpdateUserRelatedFieldsDto.builder()
                .email("test@admin.test")
                .firstname("updatedName")
                .lastname("updatedLastName")
                .phoneNumber("12312312312")
                .build();
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(fieldsToUpdate))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isAccepted());
        BDDMockito.then(usersService)
                .should()
                .updateFieldsByUserId(
                        userId,
                        fieldsToUpdate.getEmail(),
                        fieldsToUpdate.getFirstname(),
                        fieldsToUpdate.getLastname(),
                        fieldsToUpdate.getPhoneNumber()
                );
    }



}
