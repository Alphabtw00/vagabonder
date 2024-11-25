package com.vagabonder.repository;

import com.vagabonder.entity.Itinerary;
import com.vagabonder.entity.User;
import com.vagabonder.enums.ItineraryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItineraryRepository extends JpaRepository<Itinerary, UUID> {
    List<Itinerary> findByUserAndStatusOrderByToDate(User user, ItineraryStatus status); //used to get past itineraries for a user
    List<Itinerary> findByUserAndStatusOrderByFromDateDesc(User user, ItineraryStatus status); //used to get future itineraries for a user
    List<Itinerary> findByStatus(ItineraryStatus status);
}
