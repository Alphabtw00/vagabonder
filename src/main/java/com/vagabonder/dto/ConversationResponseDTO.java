package com.vagabonder.dto;

import com.vagabonder.enums.ConversationStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConversationResponseDTO {
    private ConversationStatus type;
    private ConversationDTO conversation;
    private ChatRequestDTO chatRequest;
}
