package com.globsest.testworkcreditcards.repository;

import com.globsest.testworkcreditcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    Boolean existsByEmail(String email);

}
