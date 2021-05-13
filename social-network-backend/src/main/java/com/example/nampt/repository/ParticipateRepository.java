package com.example.nampt.repository;

import com.example.nampt.entity.Participate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipateRepository extends JpaRepository<Participate, Integer> {

    List<Participate> findAllByConversationId(int conversationId);
    List<Participate> findAllByUserId(int userId);
    Participate findByConversationIdAndUserId(int conversationId,int userId);
}
