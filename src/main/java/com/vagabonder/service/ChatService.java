package com.vagabonder.service;


import com.vagabonder.dto.*;
import com.vagabonder.entity.ChatRequest;
import com.vagabonder.entity.Conversation;
import com.vagabonder.entity.Message;
import com.vagabonder.entity.User;
import com.vagabonder.enums.ConversationStatus;
import com.vagabonder.enums.FriendshipStatus;
import com.vagabonder.enums.MessageDirection;
import com.vagabonder.enums.MessageType;
import com.vagabonder.exception.UnauthorizedException;
import com.vagabonder.repository.ChatRequestRepository;
import com.vagabonder.repository.ConversationRepository;
import com.vagabonder.repository.MessageRepository;
import com.vagabonder.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ChatService {
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ChatRequestRepository chatRequestRepository;
    private final UserRepository userRepository;
    private final FriendRequestService friendRequestService;

    public ChatService(ConversationRepository conversationRepository, MessageRepository messageRepository, ChatRequestRepository chatRequestRepository, UserRepository userRepository, FriendRequestService friendRequestService) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.chatRequestRepository = chatRequestRepository;
        this.userRepository = userRepository;
        this.friendRequestService = friendRequestService;
    }


    //used when clicked message button on profile
    public ConversationResponseDTO initiateChat(UUID currentUserId, UUID otherUserId) {
        User currentUser = userRepository.findById(currentUserId).orElseThrow(() -> new IllegalArgumentException("Current User not found"));
        User otherUser = userRepository.findById(otherUserId).orElseThrow(() -> new IllegalArgumentException("Other User not found"));

        //check existing conversation
        Optional<Conversation> existingConversation = conversationRepository.findByUser1AndUser2OrUser2AndUser1(currentUser, otherUser, currentUser, otherUser);
        //if present opens the conversation
        if (existingConversation.isPresent()) {
            return ConversationResponseDTO.builder()
                    .type(ConversationStatus.EXISTING)
                    .conversation(mapConversationToDTO(existingConversation.get(), currentUserId))
                    .build();
        }

        //check friendship
        FriendshipStatus friendshipStatus = friendRequestService.getFriendshipStatus(currentUser, otherUser);

        // If friends but no conversation exists, allow to start new conversation
        if (friendshipStatus == FriendshipStatus.FRIENDS) {
            return ConversationResponseDTO.builder()
                    .type(ConversationStatus.NEW)
                    .build();
        }


        // If not friends, check for existing chat requests

        //check if we sent a request
        Optional<ChatRequest> sentRequest = chatRequestRepository.findBySenderAndReceiver(currentUser, otherUser);
        if (sentRequest.isPresent()) {
            return ConversationResponseDTO.builder()
                    .type(ConversationStatus.MESSAGE_REQUEST_SENT)
                    .chatRequest(mapChatRequestToDTO(sentRequest.get(), currentUserId))
                    .build();
        }

        //check if we received a request
        Optional<ChatRequest> receivedRequest = chatRequestRepository.findBySenderAndReceiver(otherUser, currentUser);
        if (receivedRequest.isPresent()) {
            return ConversationResponseDTO.builder()
                    .type(ConversationStatus.MESSAGE_REQUEST_RECEIVED)
                    .chatRequest(mapChatRequestToDTO(receivedRequest.get(), currentUserId))
                    .build();
        }

        //if nothing, allow to send a new chat request
        return ConversationResponseDTO.builder()
                .type(ConversationStatus.NEW_REQUEST_ALLOWED)
                .build();
    }


    @Transactional
    public MessageResponseDTO sendMessage(UUID senderId, UUID receiverId, String content) {
        User sender = userRepository.findById(senderId).orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        //check if conversation already present
        Optional<Conversation> existingConversation = conversationRepository.findByUser1AndUser2OrUser2AndUser1(sender, receiver, sender, receiver);
        if (existingConversation.isPresent()) {
            Conversation conversation = existingConversation.get();
            Message message = messageRepository.save(Message.builder()
                    .sender(sender)
                    .conversation(conversation)
                    .content(content)
                    .build());

            conversation.getMessages().add(0, message);
            conversation.setLastMessageAt(LocalDateTime.now());
            conversationRepository.save(conversation);

            return MessageResponseDTO.builder()
                    .messageType(MessageType.NORMAL)
                    .messageDTO(mapMessageToDTO(message, senderId))
                    .build();
        }

        //check friendship
        FriendshipStatus friendshipStatus = friendRequestService.getFriendshipStatus(sender, receiver);

        //friends but no conversation
        if (friendshipStatus == FriendshipStatus.FRIENDS) {
            Conversation newConversation = conversationRepository.save(Conversation.builder()
                    .user1(sender)
                    .user2(receiver)
                    .messages(new ArrayList<>())
                    .build());

            Message firstMessage = messageRepository.save(Message.builder()
                    .sender(sender)
                    .conversation(newConversation)
                    .content(content)
                    .build());

            newConversation.getMessages().add(firstMessage);
            conversationRepository.save(newConversation);

            return MessageResponseDTO.builder()
                    .messageType(MessageType.NORMAL)
                    .messageDTO(mapMessageToDTO(firstMessage, senderId))
                    .build();
        }

        //sending message when already sent request
        Optional<ChatRequest> sentRequest = chatRequestRepository.findBySenderAndReceiver(sender, receiver);
        if (sentRequest.isPresent()) {
            throw new IllegalStateException("Chat request already sent. Waiting for acceptance.");
        }

        //sending message without accepting request
        Optional<ChatRequest> receivedRequest = chatRequestRepository.findBySenderAndReceiver(receiver, sender);
        if (receivedRequest.isPresent()) {
            throw new IllegalStateException("User has already sent you a chat request. Please respond to it first.");
        }


        //no convo or request sent
        ChatRequest chatRequest = chatRequestRepository.save(ChatRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .initialMessage(content)
                .build());

        return MessageResponseDTO.builder()
                .messageType(MessageType.CHAT_REQUEST)
                .chatRequestDTO(mapChatRequestToDTO(chatRequest, senderId))
                .build();
    }

    @Transactional
    public ConversationDTO handleChatRequest(UUID requestId, UUID currentUserId, boolean accept) {
        ChatRequest request = chatRequestRepository.findById(requestId).orElseThrow(() -> new IllegalArgumentException("Chat request not found"));

        //verify that we are accepting the request sent to us
        if (!request.getReceiver().getId().equals(currentUserId)) {
            throw new UnauthorizedException("Not authorized to handle this request");
        }

        if (accept) {
            Conversation conversation = conversationRepository.save(Conversation.builder()
                    .user1(request.getSender())
                    .user2(request.getReceiver())
                    .messages(new ArrayList<>())
                    .lastMessageAt(LocalDateTime.now())
                    .build());

            //save initial message
            Message firstMessage = messageRepository.save(Message.builder()
                    .sender(request.getSender())
                    .conversation(conversation)
                    .content(request.getInitialMessage())
                    .build());

            conversation.getMessages().add(firstMessage);
            conversationRepository.save(conversation);

            //delete chat request
            chatRequestRepository.delete(request);
            return mapConversationToDTO(conversation, currentUserId);
        } else {
            //insta delete request if declined
            chatRequestRepository.delete(request);
            return null;
        }
    }



    public List<ConversationDTO> getUserConversations(UUID currentUserId) {
        User user = userRepository.findById(currentUserId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        //add normal conversations to a new list
        List<ConversationDTO> conversations = conversationRepository
                .findByUser1OrUser2OrderByLastMessageAtDesc(user, user)
                .stream()
                .map(conv -> mapConversationToDTO(conv, currentUserId))
                .toList();
        List<ConversationDTO> allItems = new ArrayList<>(conversations);


        // Add sent chat requests as conversation-like items
        List<ConversationDTO> sentRequests = chatRequestRepository
                .findBySenderOrderBySentAtDesc(user)
                .stream()
                .map(this::mapChatRequestToConversationDTO)
                .toList();
        allItems.addAll(sentRequests);

        //latest conversations first
        return allItems.stream()
                .sorted(Comparator.comparing(ConversationDTO::getLastMessageAt).reversed())
                .toList();
    }


    public List<MessageDTO> getConversationMessages(UUID conversationId, UUID currentUserId) { //todo paginate messages per request
        Conversation conversation = conversationRepository.findById(conversationId).orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        // Verify user is part of conversation
        if (!conversation.getUser1().getId().equals(currentUserId) &&
                !conversation.getUser2().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("Not authorized to view this conversation");
        }


        return messageRepository.findByConversationOrderBySentAtDesc(conversation)
                .stream()
                .map(message -> mapMessageToDTO(message,currentUserId))
                .toList();
    }


    public List<ChatRequestDTO> getReceivedChatRequests(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        return chatRequestRepository.findByReceiverOrderBySentAtDesc(user)
                .stream()
                .map(request -> mapChatRequestToDTO(request, userId))
                .toList();
    }

    private ConversationDTO mapConversationToDTO(Conversation conversation, UUID currentUserId) {
        User otherUser = getOtherUser(conversation, currentUserId);
        Message lastMessage = conversation.getMessages().isEmpty() ? null : conversation.getMessages().get(0);

        return ConversationDTO.builder()
                .id(conversation.getId())
                .otherUser(otherUser)
                .lastMessage(lastMessage != null ? mapMessageToDTO(lastMessage, currentUserId) : null)
                .lastMessageAt(conversation.getLastMessageAt())
                .isPendingRequest(false)
                .build();
    }

    private MessageDTO mapMessageToDTO(Message message, UUID currentUserId) {
        return MessageDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .sender(message.getSender())
                .sentAt(message.getSentAt())
                .direction(message.getSender().getId().equals(currentUserId) ?
                        MessageDirection.SENT_RIGHT : MessageDirection.RECEIVED_LEFT)
                .build();
    }

    private ChatRequestDTO mapChatRequestToDTO(ChatRequest request, UUID currentUserId) {
        return ChatRequestDTO.builder()
                .id(request.getId())
                .sender(request.getSender())
                .initialMessage(request.getInitialMessage())
                .direction(request.getSender().getId().equals(currentUserId) ?
                        MessageDirection.SENT_RIGHT : MessageDirection.RECEIVED_LEFT)
                .sentAt(request.getSentAt())
                .build();
    }
    private ConversationDTO mapChatRequestToConversationDTO(ChatRequest request) {
        return ConversationDTO.builder()
                .id(request.getId())
                .otherUser(request.getReceiver())
                .lastMessage(MessageDTO.builder()
                        .id(request.getId())
                        .content(request.getInitialMessage())
                        .sender(request.getSender())
                        .sentAt(request.getSentAt())
                        .direction(MessageDirection.SENT_RIGHT)
                        .build())
                .lastMessageAt(request.getSentAt())
                .isPendingRequest(true)
                .build();
    }

    private User getOtherUser(Conversation conversation, UUID currentUserId) {
        return conversation.getUser1().getId().equals(currentUserId) ?
                conversation.getUser2() : conversation.getUser1();
    }
}
