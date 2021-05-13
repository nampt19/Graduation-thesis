package com.example.nampt.repository;

import com.example.nampt.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    @Query(value = "SELECT * FROM socialnetworkproject.message " +
            "where conversation_id=(:id) " +
            "order by create_time desc " +
            "limit 1", nativeQuery = true)
    Message getLastMessage(@Param("id") int conversationId);

    List<Message> findAllByConversationId(int id);
}
