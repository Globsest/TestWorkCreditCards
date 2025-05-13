package com.globsest.testworkcreditcards.controller;

import com.globsest.testworkcreditcards.entity.Role;
import com.globsest.testworkcreditcards.entity.User;
import com.globsest.testworkcreditcards.repository.UserRepository;
import com.globsest.testworkcreditcards.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin User Management", description = "Управление пользователями (только для ADMIN)")
public class AdminUserController {
    private final AdminUserService adminUserService;

    @Operation(
            summary = "Блокировка пользователя",
            description = "Блокирует учетную запись пользователя по ID",
            parameters = {
                    @Parameter(name = "userId", description = "ID пользователя", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь успешно заблокирован",
                            content = @Content(schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    @PutMapping("/{userId}/block")
    public User blockUser(@PathVariable Long userId) throws ChangeSetPersister.NotFoundException {
        return adminUserService.blockUser(userId);
    }

    @Operation(
            summary = "Разблокировка пользователя",
            description = "Разблокирует ранее заблокированного пользователя",
            parameters = {
                    @Parameter(name = "userId", description = "ID пользователя", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь успешно разблокирован",
                            content = @Content(schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    @PutMapping("/{userId}/unblock")
    public User unblockUser(@PathVariable Long userId) throws ChangeSetPersister.NotFoundException {
        return adminUserService.unblockUser(userId);
    }


    @Operation(
            summary = "Изменение роли пользователя",
            description = "Изменяет роль указанного пользователя",
            parameters = {
                    @Parameter(name = "userId", description = "ID пользователя", required = true, example = "1"),
                    @Parameter(name = "role", description = "Новая роль пользователя", required = true,
                            schema = @Schema(implementation = Role.class))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Роль успешно изменена",
                            content = @Content(schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    @PutMapping("/{userId}/changeRole")
    public User changeRole(@PathVariable Long userId, Role role) throws ChangeSetPersister.NotFoundException {
        return adminUserService.changeRole(userId, role);
    }

    @Operation(
            summary = "Удаление пользователя",
            description = "Полное удаление пользователя из системы",
            parameters = {
                    @Parameter(name = "userId", description = "ID пользователя", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Пользователь успешно удален"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    @DeleteMapping("/{userId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) throws ChangeSetPersister.NotFoundException {
        adminUserService.deleteUser(userId);
    }

    @Operation(
            summary = "Получение всех пользователей",
            description = "Возвращает постраничный список всех пользователей",
            parameters = {
                    @Parameter(name = "page", description = "Номер страницы", example = "0"),
                    @Parameter(name = "size", description = "Размер страницы", example = "20"),
                    @Parameter(name = "sort", description = "Поле сортировки", example = "id,asc")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список пользователей получен",
                            content = @Content(schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    @GetMapping("/allUsers")
    public Page<User> getAllUsers(Pageable pageable) {
        return adminUserService.getAllUsers(pageable);
    }

}
