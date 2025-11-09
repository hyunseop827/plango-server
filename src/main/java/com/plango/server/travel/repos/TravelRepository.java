package com.plango.server.travel.repos;

import com.plango.server.travel.entity.TravelEntity;
import com.plango.server.user.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TravelRepository extends JpaRepository<TravelEntity, Long> {
    List<TravelEntity> findByUser(UserEntity user);

    // 시작일이 지금보다 미래
    List<TravelEntity> findByUserAndTravelStartAfterOrderByTravelStartAsc(
            UserEntity user, LocalDate today);

    // 진행중 - 시작일 <= 오늘 <= 종료일
    List<TravelEntity> findByUserAndTravelStartLessThanEqualAndTravelEndGreaterThanEqualOrderByTravelStartAsc(
            UserEntity user, LocalDate today1, LocalDate today2);

    // 종료일이 과거인 경우
    List<TravelEntity> findByUserAndTravelEndBeforeOrderByTravelEndDesc(
            UserEntity user, LocalDate today);

    @EntityGraph(attributePaths = {"user"})
    Optional<TravelEntity> findByTravelId(Long travelId);

}
