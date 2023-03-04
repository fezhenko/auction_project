package org.example.usersservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.usersservice.converter.UsersConverter;
import org.example.usersservice.dto.AppUserDto;
import org.example.usersservice.model.AppUser;
import org.example.usersservice.service.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
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
}
