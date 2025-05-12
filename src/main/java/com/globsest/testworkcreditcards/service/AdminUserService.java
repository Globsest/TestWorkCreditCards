package com.globsest.testworkcreditcards.service;

import com.globsest.testworkcreditcards.entity.CardStatus;
import com.globsest.testworkcreditcards.entity.Role;
import com.globsest.testworkcreditcards.entity.User;
import com.globsest.testworkcreditcards.exceptions.UserOperationException;
import com.globsest.testworkcreditcards.repository.BankCardRepository;
import com.globsest.testworkcreditcards.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminUserService {
    private final UserRepository userRepository;
    private final BankCardRepository bankCardRepository;


    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
        bankCardRepository.deleteByUserId(userId);
    }

    public User blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isActive()) {
            throw new UserOperationException("User is already blocked", "USER BLOCKED");
        }
        if (user.getRole() == Role.ADMIN) {
            throw new UserOperationException("Cannot block ADMIN users", "CANNOT BLOCK ADMIN");
        }
        user.setActive(false);
        bankCardRepository.blockAllCardsByUser(userId, CardStatus.BLOCKED);
        return userRepository.save(user);
    }

    public User unblockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.isActive()) {
            throw new UserOperationException("User is already unblocked",  "USER ALREADY UNBLOCKED");
        }
        user.setActive(true);
        return userRepository.save(user);
    }

    public User changeRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("You are not allowed to change this role");
        }
        user.setRole(Role.ADMIN);
        return userRepository.save(user);
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}
