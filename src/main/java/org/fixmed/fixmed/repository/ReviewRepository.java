package org.fixmed.fixmed.repository;

import org.fixmed.fixmed.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByDoctor_Id(Long doctorId);
    List<Review> findByFacility_Id(Long facilityId);
    Double findAverageRatingByDoctor_Id(Long doctorId);
    Double findAverageRatingByFacility_Id(Long facilityId);
}