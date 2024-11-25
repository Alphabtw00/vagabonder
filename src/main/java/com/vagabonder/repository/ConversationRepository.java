package com.vagabonder.repository;

import com.vagabonder.entity.Conversation;
import com.vagabonder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    Optional<Conversation> findByUser1AndUser2OrUser2AndUser1(User user1A, User user2A, User user1B, User user2B);

    List<Conversation> findByUser1OrUser2OrderByLastMessageAtDesc(User user, User sameUser); //sends conversations for one user, with latest being at top


}