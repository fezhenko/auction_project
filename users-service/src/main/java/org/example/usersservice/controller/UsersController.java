package org.example.usersservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.usersservice.converter.UsersConverter;
import org.example.usersservice.dto.AppUserDto;
import org.example.usersservice.dto.CreateUserDto;
import org.example.usersservice.dto.CreateUserResponseDto;
import org.example.usersservice.model.AppUser;
import org.example.usersservice.service.UsersService;
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
    public ResponseEntity<CreateUserResponseDto> createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        CreateUserResponseDto createdUserId = new CreateUserResponseDto(
                usersService.createUser(
                    createUserDto.getEmail(),
                    createUserDto.getPassword(),
                    createUserDto.getRole()));
        return ResponseEntity.ok(createdUserId);
    }

}
