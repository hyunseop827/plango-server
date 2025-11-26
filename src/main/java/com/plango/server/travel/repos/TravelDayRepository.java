package com.plango.server.travel.repos;

import com.plango.server.travel.entity.TravelDayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for travel day.
 */
@Repository
public interface TravelDayRepository extends JpaRepository<TravelDayEntity, Integer> {
}
