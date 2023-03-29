package org.example.usersservice.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.usersservice.converter.UsersConverter;
import org.example.usersservice.dto.users.AppUserDto;
import org.example.usersservice.dto.users.CreateUserDto;
import org.example.usersservice.dto.users.CredentialsDto;
import org.example.usersservice.dto.users.UpdateBalanceResultDto;
import org.example.usersservice.dto.users.UpdatePaymentInformationDto;
import org.example.usersservice.dto.users.UpdateUserRelatedFieldsDto;
import org.example.usersservice.dto.users.UpdatePasswordDto;
import org.example.usersservice.dto.users.UserPaymentsDto;
import org.example.usersservice.dto.users.UserValidationResultDto;
import org.example.usersservice.dto.users.UserVerificationDto;
import org.example.usersservice.dto.users.ValidateUserDto;
import org.example.usersservice.dto.users.UserBalanceDto;
import org.example.usersservice.model.AppUser;
import org.example.usersservice.model.Payment;
import org.example.usersservice.service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestParam;


import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
@Tag(name = "Users", description = "API to work with users")
public class UsersController {

    private final UsersService usersService;
    private final UsersConverter usersConverter;

    @Operation(summary = "Verify user")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Users successfully found"),
                    @ApiResponse(responseCode = "404", description = "There are no users were found"),
                    @ApiResponse(responseCode = "500", description = "Something Went Wrong")
            }
    )
    @PostMapping("/verify")
    public ResponseEntity<UserVerificationDto> verifyUser(@RequestBody @Valid CredentialsDto credentials) {
        UserVerificationDto userVerificationDto =
                usersService.verifyUser(credentials.getEmail());
        if (userVerificationDto == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userVerificationDto);
    }

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
        return ResponseEntity.accepted().body(usersConverter.toDto(appUser));
    }

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

    @Operation(summary = "Get payments of user by user Id")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Payments successfully found"),
                    @ApiResponse(responseCode = "404", description = "User is not found"),
                    @ApiResponse(responseCode = "500", description = "Something Went Wrong")
            }
    )
    @GetMapping("/{userId}/payments")
    public ResponseEntity<List<UserPaymentsDto>> getPaymentsByUserId(@PathVariable Long userId) {
        List<Payment> payments = usersService.findPaymentsByUserId(userId);
        return ResponseEntity.ok(usersConverter.paymentsToDto(payments));
    }


    @Operation(summary = "Update payment information by user Id ")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "202", description = "Payments successfully updated"),
                    @ApiResponse(responseCode = "404", description = "Payment is not found"),
                    @ApiResponse(responseCode = "500", description = "Something Went Wrong")
            }
    )
    @PatchMapping("/{userId}/payments/{paymentId}")
    public ResponseEntity<UserPaymentsDto> updatePaymentInformationByUserId(
            @PathVariable Long userId,
            @PathVariable Long paymentId,
            @Valid @RequestBody UpdatePaymentInformationDto updatePaymentInformationDto
    ) {
        Payment payment = usersService.updatePaymentInformation(
                userId,
                paymentId,
                updatePaymentInformationDto.getCardNumber(),
                updatePaymentInformationDto.getExpirationDate()
        );
        return ResponseEntity.accepted().body(usersConverter.paymentsToDto(payment));
    }

    @Hidden
    @GetMapping("/validate")
    public ResponseEntity<AppUser> findUserByEmail(@RequestParam(value = "email") String email) {
        AppUser user = usersService.findUserByEmail(email);
        if (user == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(user);
    }

    @Hidden
    @GetMapping("/{userId}/balance")
    private ResponseEntity<UserBalanceDto> getUserBalance(@PathVariable("userId") Long userId) {
        UserBalanceDto result = usersService.getUserBalanceById(userId);
        if (result.getBalance() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(result);
    }

    @Hidden
    @PatchMapping("/{userId}/balance")
    private ResponseEntity<UpdateBalanceResultDto> updateUserBalanceAfterAuctionFinish(
            @PathVariable("userId") Long id, Double price) {
        UpdateBalanceResultDto result = usersService.updateUserBalance(id, price);
        if (result.getBalance() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(result);
    }

}
