package com.example.nampt.repository;

import com.example.nampt.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Integer> {
    List<Comment> findAllByPostId(int postId);
    Comment findById(int id);

}
