package com.gtm.gtm.assigment.repository;

import com.gtm.gtm.common.repository.SoftDeleteRepository;
import com.gtm.gtm.assigment.domain.Assignment;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends SoftDeleteRepository<Assignment, Long> {
    boolean existsByFacility_IdAndUser_IdAndActiveTrue(Long facilityId, Long userId);
    List<Assignment> findAllByUser_IdAndActiveTrue(Long userId);

    @Query(value = """
    select 
        p.id        as point_id,
        p.name      as point_name,
        p.type      as type,
        c.id        as cycle_id,
        c.name      as cycle_name
    from gtm_point p
      join gtm_facility f   on f.id = p.facility_id 
      join gtm_assignment a on a.facility_id = f.id 
                           and a.user_id = :userId 
                           and a.active = true 
      join gtm_cycle c      on c.facility_id = f.id 
                           and c.status = 'ACTIVE' 
    where f.id = :facilityId
      and not exists (
          select 1
          from gtm_measurement m
          where m.point_id = p.id
            and m.cycle_id = c.id
            and m.user_id  = :userId
      )
    order by p.name
    """, nativeQuery = true)
    List<Object[]> findTodoPoints(Long userId, Long facilityId);

    Optional<Assignment> findByUser_IdAndFacility_IdAndActiveTrue(Long userId, Long facilityId);

}
