package com.globsest.testworkcreditcards.controller;

import com.globsest.testworkcreditcards.entity.Role;
import com.globsest.testworkcreditcards.entity.User;
import com.globsest.testworkcreditcards.repository.UserRepository;
import com.globsest.testworkcreditcards.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    private final AdminUserService adminUserService;

    @PutMapping("/{userId}/block")
    public User blockUser(@PathVariable Long userId) throws ChangeSetPersister.NotFoundException {
        return adminUserService.blockUser(userId);
    }

    @PutMapping("/{userId}/unblock")
    public User unblockUser(@PathVariable Long userId) throws ChangeSetPersister.NotFoundException {
        return adminUserService.unblockUser(userId);
    }

    @PutMapping("/{userId}/changeRole")
    public User changeRole(@PathVariable Long userId, Role role) throws ChangeSetPersister.NotFoundException {
        return adminUserService.changeRole(userId, role);
    }

    @DeleteMapping("/{userId}/delete")
    public void delete(@PathVariable Long userId) throws ChangeSetPersister.NotFoundException {
        adminUserService.deleteUser(userId);
    }

    @GetMapping("/allUsers")
    public Page<User> getAllUsers(Pageable pageable) {
        return adminUserService.getAllUsers(pageable);
    }

}
