package com.example.nampt.repository;

import com.example.nampt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByPhone(String phoneNumber);

    User findByAccessToken(String token);

    User findById(int id);

    List<User> findByNameContaining( String characters);
}
