package com.gtm.gtm.user.controller;

import com.gtm.gtm.user.domain.UserRole;
import com.gtm.gtm.user.domain.UserStatus;
import com.gtm.gtm.user.dto.*;
import com.gtm.gtm.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@Tag(name = "Users", description = "Управление пользователями")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    // --- Создать пользователя (ADMIN) — как было ---
    @Operation(summary = "Создать пользователя")
    @ApiResponse(responseCode = "201", description = "Created",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDto.class)))
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserCreateDto dto) {
        var created = service.create(dto);
        return ResponseEntity.created(URI.create("/api/users/" + created.id())).body(created);
    }

    // --- Получить пользователя по id (ADMIN) — как было ---
    @Operation(summary = "Получить пользователя по id")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto get(@PathVariable Long id) {
        return service.get(id);
    }

    // --- Текущий пользователь (по JWT) — как было ---
    @Operation(summary = "Текущий пользователь (по JWT)")
    @GetMapping("/me")
    public UserDto me(@AuthenticationPrincipal Jwt jwt) {
        return service.getBySubject(jwt.getSubject());
    }

    // --- Обновить СЕБЯ ---
    @Operation(summary = "Обновить собственный профиль")
    @PutMapping("/me")
    public UserDto updateMe(@AuthenticationPrincipal Jwt jwt,
                            @Valid @RequestBody UserSelfUpdateDto dto) {
        return service.updateSelfBySubject(jwt.getSubject(), dto);
    }

    // --- Смена своего пароля — как было ---
    @Operation(summary = "Сменить свой пароль")
    @PostMapping("/me/password")
    public ResponseEntity<Void> changeOwnPassword(@AuthenticationPrincipal Jwt jwt,
                                                  @Valid @RequestBody ChangePasswordDto req) {
        service.changePasswordBySubject(jwt.getSubject(), req.newPassword());
        return ResponseEntity.noContent().build();
    }

    // --- Листинг пользователей (ADMIN) ---
    @Operation(summary = "Список пользователей (пагинация)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserDto> list(
            @Parameter(hidden = true)
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return service.list(pageable);
    }

    // --- Обновление пользователя (ADMIN) ---
    @Operation(summary = "Обновить пользователя (ADMIN)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto adminUpdate(@PathVariable Long id, @Valid @RequestBody UserAdminUpdateDto dto) {
        return service.adminUpdate(id, dto);
    }

    // --- Смена статуса (ADMIN) ---
    @Operation(summary = "Сменить статус пользователя (ADMIN)")
    @PostMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> changeStatus(@PathVariable Long id,
                                             @Valid @RequestBody ChangeStatusDto req) {
        service.changeStatus(id, req.status());
        return ResponseEntity.noContent().build();
    }

    // --- Смена ролей (ADMIN) ---
    @Operation(summary = "Сменить роли пользователя (ADMIN)")
    @PostMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto changeRoles(@PathVariable Long id, @Valid @RequestBody ChangeRolesDto req) {
        return service.changeRoles(id, req.roles());
    }

    // --- Soft delete (ADMIN) ---
    @Operation(summary = "Удалить пользователя (soft delete, ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- Справочник ролей ---
    @Operation(summary = "Список ролей (ключ -> русское название)")
    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> rolesDict() {
        var m = new LinkedHashMap<String, String>();
        m.put(UserRole.ADMIN.name(), "Администратор");
        m.put(UserRole.KAMERAL.name(), "Камеральный специалист");
        m.put(UserRole.MANAGER.name(), "Менеджер");
        return m;
    }

    // --- Справочник статусов ---
    @Operation(summary = "Список статусов (ключ -> русское название)")
    @GetMapping("/statuses")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> statusesDict() {
        var m = new LinkedHashMap<String, String>();
        m.put(UserStatus.ACTIVE.name(),  "Активен");
        m.put(UserStatus.BLOCKED.name(), "Заблокирован");
        return m;
    }
}