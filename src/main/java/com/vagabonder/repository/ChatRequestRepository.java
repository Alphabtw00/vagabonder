package com.vagabonder.repository;

import com.vagabonder.entity.ChatRequest;
import com.vagabonder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRequestRepository extends JpaRepository<ChatRequest, UUID> {
    List<ChatRequest> findByReceiverOrderBySentAtDesc(User receiver);
    List<ChatRequest> findBySenderOrderBySentAtDesc(User sender);
    Optional<ChatRequest> findBySenderAndReceiver(User sender, User receiver);
}