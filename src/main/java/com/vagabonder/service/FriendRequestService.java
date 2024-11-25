package com.vagabonder.service;

import com.vagabonder.entity.FriendRequest;
import com.vagabonder.entity.User;
import com.vagabonder.enums.FriendshipStatus;
import com.vagabonder.repository.FriendRequestRepository;
import com.vagabonder.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FriendRequestService {
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;

    public FriendRequestService(UserRepository userRepository, FriendRequestRepository friendRequestRepository) {
        this.userRepository = userRepository;
        this.friendRequestRepository = friendRequestRepository;
    }

    public void sendFriendRequest(UUID senderId, UUID receiverId) { //sender is current user
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("You cannot send Friend Request to yourself");
        }
        User sender = userRepository.findById(senderId).orElseThrow(() -> new IllegalArgumentException("Sender User not found"));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new IllegalArgumentException("Receiver User not found"));

        //if received friend request, then accept it
        Optional<FriendRequest> existingRequest = friendRequestRepository.findBySenderAndReceiverAndAccepted(receiver, sender, false);
        if (existingRequest.isPresent()) {
            existingRequest.get().setAccepted(true);
            friendRequestRepository.save(existingRequest.get());
            return;
        }

        //if already friends
        if (friendRequestRepository.existsBySenderAndReceiverAndAccepted(sender, receiver, true) ||
                friendRequestRepository.existsBySenderAndReceiverAndAccepted(receiver, sender, true)) {
            throw new IllegalArgumentException("You are already friends with this user");
        }

        //if request already sent
        if (friendRequestRepository.existsBySenderAndReceiverAndAccepted(sender, receiver, false)) {
            throw new IllegalArgumentException("You have already sent a friend request to this user");
        }

        FriendRequest friendRequest = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .accepted(false)
                .build();
        friendRequestRepository.save(friendRequest);
    }


    public void acceptFriendRequest(UUID currentUserId, UUID userId) {
        if (currentUserId.equals(userId)) {
            throw new IllegalArgumentException("You cannot be friends with yourself");
        }

        User sender = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Sender User not found"));
        User receiver = userRepository.findById(currentUserId).orElseThrow(() -> new IllegalArgumentException("Receiver User not found"));

        FriendRequest request = friendRequestRepository.findBySenderAndReceiverAndAccepted(sender,receiver,false)
                .orElseThrow(() -> new IllegalArgumentException("Friend Request not found"));
        request.setAccepted(true);
        friendRequestRepository.save(request);
    }

    public void rejectFriendRequest(UUID currentUserId, UUID userId) {
        if (currentUserId.equals(userId)) {
            throw new IllegalArgumentException("You cannot reject yourself");
        }
        User sender = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Sender User not found"));
        User receiver = userRepository.findById(currentUserId).orElseThrow(() -> new IllegalArgumentException("Receiver User not found"));

        FriendRequest request = friendRequestRepository.findBySenderAndReceiverAndAccepted(sender,receiver,false)
                .orElseThrow(() -> new IllegalArgumentException("Friend Request not found"));
        friendRequestRepository.delete(request);
    }

    public List<FriendRequest> getPendingSentRequests(UUID currentUserId) {
        User currentUser = userRepository.findById(currentUserId).orElseThrow(() -> new IllegalArgumentException("Current User not found"));
        return friendRequestRepository.findBySenderAndAccepted(currentUser, false);
    }

    public List<FriendRequest> getPendingReceivedRequests(UUID currentUserId) {
        User currentUser = userRepository.findById(currentUserId).orElseThrow(() -> new IllegalArgumentException("Current User not found"));
        return friendRequestRepository.findByReceiverAndAccepted(currentUser, false);
    }

    public List<User> getUserFriends(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Get friends where user is the sender
        List<FriendRequest> sentAccepted = friendRequestRepository.findBySenderAndAccepted(user, true);
        List<User> friends = new ArrayList<>(sentAccepted.stream().map(FriendRequest::getReceiver).toList());

        // Get friends where user is the receiver
        List<FriendRequest> receivedAccepted = friendRequestRepository.findByReceiverAndAccepted(user, true);
        friends.addAll(receivedAccepted.stream().map(FriendRequest::getSender).toList());

        return friends;
    }

    public FriendshipStatus getFriendshipStatus(User currentUser, User profileUser) {
        if (currentUser.getId().equals(profileUser.getId())) {
            return FriendshipStatus.SELF;
        } else if (friendRequestRepository.existsBySenderAndReceiverAndAccepted(currentUser, profileUser, true) ||
                friendRequestRepository.existsBySenderAndReceiverAndAccepted(profileUser, currentUser, true)) {
            return FriendshipStatus.FRIENDS;
        } else if (friendRequestRepository.existsBySenderAndReceiverAndAccepted(currentUser, profileUser, false)) {
            return FriendshipStatus.PENDING_REQUEST_SENT;
        } else if (friendRequestRepository.existsBySenderAndReceiverAndAccepted(profileUser, currentUser, false)) {
            return FriendshipStatus.PENDING_REQUEST_RECEIVED;
        } else {
            return FriendshipStatus.NOT_FRIENDS;
        }
    }


}
