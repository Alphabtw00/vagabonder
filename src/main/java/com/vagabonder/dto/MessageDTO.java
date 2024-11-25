package com.vagabonder.dto;


import com.vagabonder.entity.User;
import com.vagabonder.enums.MessageDirection;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class MessageDTO {
    private UUID id;
    private User sender;
    private String content;
    private MessageDirection direction;
    private LocalDateTime sentAt;
}
