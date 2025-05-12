package com.globsest.testworkcreditcards.service;

import com.globsest.testworkcreditcards.entity.CardStatus;
import com.globsest.testworkcreditcards.entity.Role;
import com.globsest.testworkcreditcards.entity.User;
import com.globsest.testworkcreditcards.exceptions.UserOperationException;
import com.globsest.testworkcreditcards.repository.BankCardRepository;
import com.globsest.testworkcreditcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BankCardRepository bankCardRepository;

    @InjectMocks
    private AdminUserService adminUserService;

    @Test
    void blockUser_shouldSuccess() {
        Long userId = 1L;
        User user = new User();
        user.setActive(true);
        user.setRole(Role.USER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        User result = adminUserService.blockUser(userId);

        assertFalse(result.isActive());
        verify(bankCardRepository).blockAllCardsByUser(userId, CardStatus.BLOCKED);
    }

    @Test
    void blockUser_shouldThrowWhenAlreadyBlocked() {
        Long userId = 1L;
        User user = new User();
        user.setActive(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(UserOperationException.class, () -> adminUserService.blockUser(userId));
    }

    @Test
    void changeRole_shouldSuccess() {
        Long userId = 1L;
        User user = new User();
        user.setRole(Role.USER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        User result = adminUserService.changeRole(userId, Role.ADMIN);

        assertEquals(Role.ADMIN, result.getRole());
    }
}