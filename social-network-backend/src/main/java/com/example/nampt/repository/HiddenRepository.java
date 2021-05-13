package com.example.nampt.repository;

import com.example.nampt.entity.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HiddenRepository extends JpaRepository<Hidden, Integer> {
    List<Hidden> findAllByPostId(int postId);
    Hidden findByPostIdAndHiddenUserId(int postId,int hiddenUserId);
}
