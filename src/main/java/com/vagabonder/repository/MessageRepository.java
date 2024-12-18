package com.vagabonder.repository;

import com.vagabonder.entity.Conversation;
import com.vagabonder.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByConversationOrderBySentAtDesc(Conversation conversation);
}
