package com.gtm.gtm.user.controller;

import com.gtm.gtm.user.dto.ChangePasswordDto;
import com.gtm.gtm.user.dto.UserCreateDto;
import com.gtm.gtm.user.dto.UserDto;
import com.gtm.gtm.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@Tag(name = "Users", description = "Управление пользователями")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @Operation(summary = "Создать пользователя")
    @ApiResponse(responseCode = "201", description = "Created",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDto.class)))
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserCreateDto dto) {
        var created = service.create(dto);
        return ResponseEntity
                .created(URI.create("/api/users/" + created.id()))
                .body(created);
    }

    @Operation(summary = "Получить пользователя по id")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto get(@PathVariable Long id) {
        return service.get(id);
    }

    // Текущий пользователь из JWT (ожидаем, что sub = userId или email)
    @Operation(summary = "Текущий пользователь (по JWT)")
    @GetMapping("/me")
    public UserDto me(@AuthenticationPrincipal Jwt jwt) {
        return service.getBySubject(jwt.getSubject());
    }

    // Смена пароля самим пользователем
    @Operation(summary = "Сменить свой пароль")
    @PostMapping("/me/password")
    public ResponseEntity<Void> changeOwnPassword(@AuthenticationPrincipal Jwt jwt,
                                                  @Valid @RequestBody ChangePasswordDto req) {
        service.changePasswordBySubject(jwt.getSubject(), req.newPassword());
        return ResponseEntity.noContent().build();
    }

    // Админ меняет пароль любому пользователю
    @Operation(summary = "Сменить пароль пользователю (ADMIN)")
    @PostMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> changePassword(@PathVariable Long id,
                                               @Valid @RequestBody ChangePasswordDto req) {
        service.changePassword(id, req.newPassword());
        return ResponseEntity.noContent().build();
    }
}