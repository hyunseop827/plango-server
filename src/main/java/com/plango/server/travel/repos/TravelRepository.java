package com.plango.server.travel.repos;

import com.plango.server.travel.entity.TravelEntity;
import com.plango.server.user.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for travel.
 * Returning records in order
 */
@Repository
public interface TravelRepository extends JpaRepository<TravelEntity, Long> {
    List<TravelEntity> findByUser(UserEntity user);

    List<TravelEntity> findByUserAndTravelStartAfterOrderByTravelStartAsc(
            UserEntity user, LocalDate today);

    List<TravelEntity> findByUserAndTravelStartLessThanEqualAndTravelEndGreaterThanEqualOrderByTravelStartAsc(
            UserEntity user, LocalDate today1, LocalDate today2);

    List<TravelEntity> findByUserAndTravelEndBeforeOrderByTravelEndDesc(
            UserEntity user, LocalDate today);

    @EntityGraph(attributePaths = {"user", "travelDays", "travelDays.courses"})
    Optional<TravelEntity> findByTravelId(Long travelId);

}
