package org.fixmed.fixmed.repository;

import org.fixmed.fixmed.model.Doctors;
import org.fixmed.fixmed.model.Facilities;
import org.fixmed.fixmed.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilitiesRepository extends JpaRepository<Facilities, Long> {
    @Query("""
    SELECT f FROM Facilities f
    WHERE (:name IS NULL OR f.name LIKE %:name%)
    AND (:address IS NULL OR f.address LIKE %:address%)
    """)
    Page<Facilities> searchFacilities(
            @Param("name") String name,
            @Param("address") String address,
            Pageable pageable
    );


}
