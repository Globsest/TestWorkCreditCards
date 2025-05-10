package com.globsest.testworkcreditcards.service;

import com.globsest.testworkcreditcards.entity.User;
import com.globsest.testworkcreditcards.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow();

        return UserDetailsImpl.build(user);
    }

    public UserDetails loadUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new UsernameNotFoundException("User not found"));

        return UserDetailsImpl.build(user);
    }
}
