package com.vagabonder.dto;

import com.vagabonder.enums.MessageType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageResponseDTO {
    private MessageType messageType;
    private MessageDTO messageDTO;
    private ChatRequestDTO chatRequestDTO;
}
