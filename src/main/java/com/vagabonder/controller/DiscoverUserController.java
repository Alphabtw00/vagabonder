package com.vagabonder.controller;

import com.vagabonder.dto.DiscoveryResponseDTO;
import com.vagabonder.entity.User;
import com.vagabonder.service.DiscoverUserService;
import com.vagabonder.service.FriendRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
public class DiscoverUserController {
    private final DiscoverUserService discoverUserService;
    private final FriendRequestService friendRequestService;

    public DiscoverUserController(DiscoverUserService discoverUserService, FriendRequestService friendRequestService) {
        this.discoverUserService = discoverUserService;
        this.friendRequestService = friendRequestService;
    }

    @GetMapping({"/discover", "/discover/{lastSeenId}"})
    public ResponseEntity<DiscoveryResponseDTO> getSimilarUsers(@AuthenticationPrincipal User currentUser, @PathVariable(required = false) String lastSeenId) { //todo send UserDto instead of user
        DiscoveryResponseDTO responseDTO = discoverUserService.discoverUsers(currentUser.getId(),lastSeenId, 30); //set whatever amount of user we need per request
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/ignore/{userId}")
    public ResponseEntity<Void> ignoreUser(@AuthenticationPrincipal User currentUser, @PathVariable(name = "userId") UUID ignoreUserId) {
        discoverUserService.ignoreUser(currentUser.getId(), ignoreUserId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ignore")
    public ResponseEntity<Void> ignoreMultipleUsers(@AuthenticationPrincipal User currentUser, @RequestBody List<UUID> ignoredUserIds) {
        discoverUserService.ignoreMultipleUsers(currentUser.getId(), ignoredUserIds);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add-friend/{userId}")
    public ResponseEntity<String> sendFriendRequest(@AuthenticationPrincipal User currentUser, @PathVariable UUID userId) {  //use this when clicked tick
        friendRequestService.sendFriendRequest(currentUser.getId(), userId);
        return ResponseEntity.ok("Sent Friend Request to " + userId);
    }

    @PostMapping("/accept-friend/{userId}")
    public ResponseEntity<String > acceptFriendRequest(@AuthenticationPrincipal User currentUser, @PathVariable UUID userId) {
        friendRequestService.acceptFriendRequest(currentUser.getId(), userId);
        return ResponseEntity.ok("Accepted Friend Request from " + userId);
    }


    @PostMapping("/reject-friend/{userId}")
    public ResponseEntity<String> rejectFriendRequest(@AuthenticationPrincipal User currentUser, @PathVariable UUID userId) {
        friendRequestService.rejectFriendRequest(currentUser.getId(), userId);
        return ResponseEntity.ok("Rejected Friend Request from " + userId);
    }


}
