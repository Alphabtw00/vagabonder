package com.vagabonder.dto;

import com.vagabonder.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ConversationDTO {
    private UUID id;
    private User otherUser;
    private MessageDTO lastMessage; //used to show preview of last message
    private LocalDateTime lastMessageAt;
    private boolean isPendingRequest;
}
