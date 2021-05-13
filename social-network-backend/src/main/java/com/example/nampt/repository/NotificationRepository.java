package com.example.nampt.repository;

import com.example.nampt.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Integer> {
        Notification findById(int id);
        List<Notification> findAllByUserId(int userId);
}
