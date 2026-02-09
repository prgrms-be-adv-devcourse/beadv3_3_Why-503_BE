package io.why503.performanceservice.domain.hall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.why503.performanceservice.domain.hall.model.entity.HallEntity;

@Repository
public interface HallRepository extends JpaRepository<HallEntity, Long> {
    //이름과 주소로 같은 공연장인지 여부 판단
    boolean existsByNameAndBasicAddr(String name, String basicAddr);
}
