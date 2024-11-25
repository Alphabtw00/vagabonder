package com.vagabonder.repository;

import com.vagabonder.entity.FriendRequest;
import com.vagabonder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, UUID> { // findBy[Property][Operator][Condition] default
    boolean existsBySenderAndReceiver(User sender, User receiver); //checks if user has sent a friend request or is a friend, both

    boolean existsBySenderAndReceiverAndAccepted(User sender, User receiver, boolean accepted); //accepted = true(users already friends), false(pending request)

    Optional<FriendRequest> findBySenderAndReceiverAndAccepted(User sender, User receiver, boolean accepted);

    List<FriendRequest> findBySenderAndAccepted(User user, boolean b); // b=false, used to get pending sent friend requests

    List<FriendRequest> findByReceiverAndAccepted(User user, boolean b); // b=false, used to get pending received friend requests

}
