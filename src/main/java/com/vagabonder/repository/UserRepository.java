package com.vagabonder.repository;

import com.vagabonder.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> { // findBy[Property][Operator][Condition] default
    Optional<User> findByEmailEquals(String email);
    boolean existsByEmailEquals(String email);

    long countByAgeBetweenAndIdNotIn(int minAge, int maxAge, Set<UUID> ignoredUserIds);

    Page<User> findByAgeBetweenAndIdNotIn(
            int minAge,
            int maxAge,
            Set<UUID> excludeIds,
            Pageable pageable
    );
}
