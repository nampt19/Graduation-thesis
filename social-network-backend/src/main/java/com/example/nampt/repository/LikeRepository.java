package com.example.nampt.repository;

import com.example.nampt.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like,Integer> {
    List<Like> findAllByPostId(int postId);
    Like findByPostIdAndSenderId(int postId,int senderId);
}
