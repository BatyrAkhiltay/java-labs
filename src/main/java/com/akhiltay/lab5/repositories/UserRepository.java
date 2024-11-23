package com.akhiltay.lab5.repositories;

import com.akhiltay.lab5.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
