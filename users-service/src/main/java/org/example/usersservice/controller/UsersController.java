package org.example.usersservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.usersservice.converter.UsersConverter;
import org.example.usersservice.dto.AppUserDto;
import org.example.usersservice.dto.CreateUserDto;
import org.example.usersservice.dto.ValidateUserDto;
import org.example.usersservice.dto.UserValidationResultDto;
import org.example.usersservice.dto.UpdateUserRelatedFieldsDto;
import org.example.usersservice.dto.UpdatePasswordDto;
import org.example.usersservice.model.AppUser;
import org.example.usersservice.service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
@Tag(name = "Users", description = "API to work with users")
public class UsersController {

    private final UsersService usersService;
    private final UsersConverter usersConverter;

    @Tag(name = "Users")
    @Operation(summary = "Get all existed users from users database")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Users successfully found"),
                    @ApiResponse(responseCode = "404", description = "There are no users were found"),
                    @ApiResponse(responseCode = "500", description = "Something Went Wrong")
            }
    )
    @GetMapping
    public ResponseEntity<List<AppUserDto>> findUsers() {
        final List<AppUser> appUsers = usersService.findUsers();
        return ResponseEntity.ok(usersConverter.toDto(appUsers));
    }

    @Tag(name = "Users")
    @Operation(summary = "Get user from database by userId")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User successfully found"),
                    @ApiResponse(responseCode = "404", description = "User is not found"),
                    @ApiResponse(responseCode = "500", description = "Something Went Wrong")
            }
    )
    @GetMapping("/{userId}")
    public ResponseEntity<AppUserDto> getUserById(@PathVariable Long userId) {
        AppUser appUser = usersService.getUserById(userId);
        return ResponseEntity.ok(usersConverter.toDto(appUser));
    }

    @Tag(name = "Users")
    @Operation(summary = "Create user by email, password, role")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "User successfully created"),
                    @ApiResponse(responseCode = "400", description = "Check the input and try again"),
                    @ApiResponse(responseCode = "500", description = "Something Went Wrong")
            }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        usersService.createUser(
                createUserDto.getEmail(),
                createUserDto.getPassword(),
                createUserDto.getRole());
    }

    @Tag(name = "Users")
    @Operation(summary = "Update fields related to user by user id")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User successfully found"),
                    @ApiResponse(responseCode = "404", description = "User is not found"),
                    @ApiResponse(responseCode = "500", description = "Something Went Wrong")
            }
    )
    @PutMapping("/{userId}")
    public ResponseEntity<AppUserDto> updateUserRelatedFields(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRelatedFieldsDto updateUserRelatedFieldsDto) {
        usersService.updateFieldsByUserId(
                userId,
                updateUserRelatedFieldsDto.getEmail(),
                updateUserRelatedFieldsDto.getFirstname(),
                updateUserRelatedFieldsDto.getLastname(),
                updateUserRelatedFieldsDto.getPhoneNumber()
        );
        AppUser appUser = usersService.getUserById(userId);
        return ResponseEntity.ok(usersConverter.toDto(appUser));
    }

    @Tag(name = "Users")
    @Operation(summary = "Update user password by user id")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User password is updated"),
                    @ApiResponse(responseCode = "404", description = "User is not found"),
                    @ApiResponse(responseCode = "500", description = "Something Went Wrong")
            }
    )
    @PatchMapping("/{userId}/update-password")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateUserPassword(
            @PathVariable Long userId,
            @Valid @RequestBody UpdatePasswordDto updatePasswordDto
    ) {
        usersService.updateUserPassword(
                userId,
                updatePasswordDto.getPassword()
        );
    }

    @Tag(name = "Users")
    @Operation(summary = "Delete user by id")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "User successfully found"),
                    @ApiResponse(responseCode = "404", description = "User is not found"),
                    @ApiResponse(responseCode = "500", description = "Something Went Wrong")
            }
    )
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteUserById(
            @PathVariable Long userId
    ) {
        usersService.deleteUserById(userId);
    }

    @PostMapping("/validate")
    public ResponseEntity<UserValidationResultDto> validateUserByEmail(
            @RequestBody ValidateUserDto validateUserDto
    ) {
        UserValidationResultDto validationResult =
                UserValidationResultDto.builder()
                        .isValid(usersService.validateUserByEmail(validateUserDto.getEmail()))
                        .build();
        return ResponseEntity.ok(validationResult);
    }

}
