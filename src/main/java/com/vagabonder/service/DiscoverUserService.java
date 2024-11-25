package com.vagabonder.service;

import com.vagabonder.dto.DiscoveryResponseDTO;
import com.vagabonder.entity.User;
import com.vagabonder.enums.FriendshipStatus;
import com.vagabonder.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DiscoverUserService {
    private final UserRepository userRepository;
    private final FriendRequestService friendRequestService;
    private final Random random = new Random();

    public DiscoverUserService(UserRepository userRepository, FriendRequestService friendRequestService) {
        this.userRepository = userRepository;
        this.friendRequestService = friendRequestService;
    }

    public DiscoveryResponseDTO discoverUsers(UUID currentUserId, String pageToken, int batchSize) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        int minAge = currentUser.getAge() - 5;
        int maxAge = currentUser.getAge() + 5;

        Set<UUID> excludeIds = new HashSet<>(currentUser.getIgnoredUserIds());

        // Get total count of potential matches
        long totalCount = userRepository.countByAgeBetweenAndIdNotIn(
                minAge,
                maxAge,
                excludeIds
        );

        if (totalCount == 0) {
            return new DiscoveryResponseDTO(List.of(), null);
        }

        // Calculate starting position
        int offset;
        if (pageToken != null && !pageToken.isEmpty()) {
            offset = decodePageToken(pageToken);
            // If offset is beyond total count, generate new random position
            if (offset >= totalCount) {
                offset = random.nextInt((int) totalCount);
            }
        } else {
            // Random start position for fresh requests
            offset = random.nextInt((int) totalCount);
        }

        // Get two pages worth of users to ensure we have enough after filtering
        int expandedBatchSize = batchSize * 2;
        Pageable pageable = PageRequest.of(offset / expandedBatchSize, expandedBatchSize);

        Page<User> userPage = userRepository.findByAgeBetweenAndIdNotIn(
                minAge,
                maxAge,
                excludeIds,
                pageable
        );

        // Filter users based on friendship status
        List<User> filteredUsers = userPage.getContent().stream()
                .filter(user -> Set.of(
                        FriendshipStatus.NOT_FRIENDS,
                        FriendshipStatus.PENDING_REQUEST_RECEIVED
                ).contains(friendRequestService.getFriendshipStatus(currentUser, user)))
                .collect(Collectors.toList());

        // Shuffle the filtered results
        Collections.shuffle(filteredUsers);

        // Take only what we need
        List<User> finalUsers = filteredUsers.stream()
                .limit(batchSize)
                .collect(Collectors.toList());

        // Only generate next token if we have more results
        String nextPageToken = null;
        if (userPage.hasNext() && finalUsers.size() == batchSize) {
            nextPageToken = encodePageToken(offset + batchSize);
        }

        return new DiscoveryResponseDTO(finalUsers, nextPageToken);
    }

    private String encodePageToken(int offset) {
        return Base64.getEncoder().encodeToString(String.valueOf(offset).getBytes());
    }

    private int decodePageToken(String pageToken) {
        try {
            String decoded = new String(Base64.getDecoder().decode(pageToken));
            return Integer.parseInt(decoded);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid page token");
        }
    }

    public void ignoreUser(UUID currentUserId, UUID ignoredUserId) {
        if (currentUserId.equals(ignoredUserId)) {
            throw new IllegalArgumentException("You cannot ignore yourself");
        }
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Current User not found"));
        currentUser.getIgnoredUserIds().add(ignoredUserId);
        userRepository.save(currentUser);
    }

    public void ignoreMultipleUsers(UUID currentUserId, List<UUID> ignoredUserIds) {
        if (ignoredUserIds.contains(currentUserId)) {
            throw new IllegalArgumentException("You cannot ignore yourself");
        }
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Current User not found"));

        currentUser.getIgnoredUserIds().addAll(ignoredUserIds);
        userRepository.save(currentUser);
    }


}
