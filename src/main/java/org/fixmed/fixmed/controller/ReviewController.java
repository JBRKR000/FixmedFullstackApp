package org.fixmed.fixmed.controller;

import lombok.RequiredArgsConstructor;
import org.fixmed.fixmed.model.*;
import org.fixmed.fixmed.model.dto.ReviewDto;
import org.fixmed.fixmed.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final DoctorsRepository doctorsRepository;
    private final FacilitiesRepository facilitiesRepository;
    private final UsersRepository usersRepository;

    @PostMapping("/doctors/{id}/reviews")
    public ResponseEntity<?> addDoctorReview(@PathVariable Long id, @RequestBody ReviewDto dto) {
        var doctor = doctorsRepository.findById(id).orElse(null);
        var user = usersRepository.findById(dto.getUserId()).orElse(null);
        if (doctor == null || user == null) return ResponseEntity.badRequest().build();

        Review review = Review.builder()
                .doctor(doctor)
                .user(user)
                .rating(dto.getRating())
                .comment(dto.getComment())
                .createdAt(LocalDateTime.now())
                .build();
        reviewRepository.save(review);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/facilities/{id}/reviews")
    public ResponseEntity<?> addFacilityReview(@PathVariable Long id, @RequestBody ReviewDto dto) {
        var facility = facilitiesRepository.findById(id).orElse(null);
        var user = usersRepository.findById(dto.getUserId()).orElse(null);
        if (facility == null || user == null) return ResponseEntity.badRequest().build();

        Review review = Review.builder()
                .facility(facility)
                .user(user)
                .rating(dto.getRating())
                .comment(dto.getComment())
                .createdAt(LocalDateTime.now())
                .build();
        reviewRepository.save(review);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/doctors/{id}/reviews")
    public ResponseEntity<List<ReviewDto>> getDoctorReviews(@PathVariable Long id) {
        List<ReviewDto> reviews = reviewRepository.findByDoctor_Id(id).stream().map(this::toDto).toList();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/facilities/{id}/reviews")
    public ResponseEntity<List<ReviewDto>> getFacilityReviews(@PathVariable Long id) {
        List<ReviewDto> reviews = reviewRepository.findByFacility_Id(id).stream().map(this::toDto).toList();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/doctors/{id}/average-rating")
    public ResponseEntity<Double> getDoctorAverageRating(@PathVariable Long id) {
        List<Review> reviews = reviewRepository.findByDoctor_Id(id);
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        return ResponseEntity.ok(avg);
    }

    @GetMapping("/facilities/{id}/average-rating")
    public ResponseEntity<Double> getFacilityAverageRating(@PathVariable Long id) {
        List<Review> reviews = reviewRepository.findByFacility_Id(id);
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        return ResponseEntity.ok(avg);
    }

    private ReviewDto toDto(Review r) {
        ReviewDto dto = new ReviewDto();
        dto.setId(r.getId());
        dto.setUserId(r.getUser().getId());
        dto.setDoctorId(r.getDoctor() != null ? r.getDoctor().getId() : null);
        dto.setFacilityId(r.getFacility() != null ? r.getFacility().getId() : null);
        dto.setRating(r.getRating());
        dto.setComment(r.getComment());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }
}