package com.vagabonder.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class SendMessageRequestDTO {
    private UUID senderId;
    private UUID receiverId;
    private String content;
}
