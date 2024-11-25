package com.vagabonder.enums;

public enum ConversationStatus {
    EXISTING,               // Conversation exists // goes to conversation page, loaded all conversation and with user on whose button we clicked, highlighted and its messages loaded
    NEW,                   // Can start new conversation (for friends) //goes to conversation page, nothing highlighted as no conversaton yet, but allows to send message
    NEW_REQUEST_ALLOWED,   // Can send chat request (for non-friends) //goes to conversation page, nothing highlighted as no conversation yet, one message allowed and then blocked, chat request sent and status changes to MESSAGE_REQUEST_SENT
    MESSAGE_REQUEST_SENT,  // We sent a request, waiting for acceptance //goes to conversation page, nothing highlighted as no conversation yet, message sent can be seen and pls wait to get accepted is present
    MESSAGE_REQUEST_RECEIVED // We received a request, need to accept/decline ////goes to request page, nothing highlighted as no conversation yet, message sent can be seen from other user and aceept button present
}
