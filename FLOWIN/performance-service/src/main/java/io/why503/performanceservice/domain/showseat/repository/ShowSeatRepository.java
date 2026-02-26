package io.why503.performanceservice.domain.showseat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.why503.performanceservice.domain.showseat.model.entity.ShowSeatEntity;
import io.why503.performanceservice.domain.showseat.model.enums.ShowSeatGrade;

import java.util.List;

public interface ShowSeatRepository extends JpaRepository<ShowSeatEntity, Long> {

    List<ShowSeatEntity> findByShow_Sq(Long showSq);
    List<ShowSeatEntity> findByShow_SqAndGrade(Long showSq, ShowSeatGrade grade);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update ShowSeatEntity s
           set s.price = :price
         where s.show.sq = :showSq
           and s.grade = :grade
    """)
    int updatePriceByShowAndGrade(
            @Param("showSq") Long showSq,
            @Param("grade") ShowSeatGrade grade,
            @Param("price") Long price
    );
}
