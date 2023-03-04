package org.example.usersservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.usersservice.converter.UsersConverter;
import org.example.usersservice.dto.AppUserDto;
import org.example.usersservice.dto.CreateUserDto;
import org.example.usersservice.dto.UpdateUserNonMandatoryFieldsDto;
import org.example.usersservice.model.AppUser;
import org.example.usersservice.service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;


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
                    @ApiResponse(responseCode = "404", description = "There are no users were found"),
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
                    @ApiResponse(responseCode = "200", description = "User successfully found"),
                    @ApiResponse(responseCode = "404", description = "There are no users were found"),
                    @ApiResponse(responseCode = "500", description = "Something Went Wrong")
            }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        usersService.createUser(
                createUserDto.getEmail(),
                createUserDto.getPassword(),
                createUserDto.getRole());
    }

    @Tag(name = "Users")
    @Operation(summary = "Update non-mandatory fields of user by user id")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User successfully found"),
                    @ApiResponse(responseCode = "404", description = "There are no users were found"),
                    @ApiResponse(responseCode = "500", description = "Something Went Wrong")
            }
    )
    @PutMapping("/{userId}")
    public ResponseEntity<AppUserDto> updateUserNonMandatoryFields(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserNonMandatoryFieldsDto userNonMandatoryFieldsDto) {
        usersService.updateNonMandatoryFieldsByUserId(
                userId,
                userNonMandatoryFieldsDto.getFirstname(),
                userNonMandatoryFieldsDto.getLastname(),
                userNonMandatoryFieldsDto.getPhoneNumber()
        );
        AppUser appUser = usersService.getUserById(userId);
        return ResponseEntity.ok(usersConverter.toDto(appUser));
    }

}
