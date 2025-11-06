package com.plango.server.travel;

import com.plango.server.travel.entity.TravelEntity;
import com.plango.server.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TravelRepository extends JpaRepository<TravelEntity, Long> {
    List<TravelEntity> findByUser(UserEntity user);
}
