package com.example.nampt.repository;

import com.example.nampt.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {

    List<Conversation> findAllByCreatorId(int creatorId);
    Conversation findById(int id);

}
