package com.triple.mileageservice.repository

import com.triple.mileageservice.domain.Mileage
import org.springframework.data.jpa.repository.JpaRepository

interface MileageRepository : JpaRepository<Mileage, Long> {
    fun countByPlaceIdAndDeletedIsFalse(placeId: String): Int
    fun existsByPlaceIdAndReviewIdAndUserIdAndDeletedIsFalse(placeId: String, reviewId: String, userId: String): Boolean
    fun findByPlaceIdAndReviewIdAndUserIdAndDeletedIsFalse(placeId: String, reviewId: String, userId: String): Mileage?
    fun findAllByPlaceIdAndDeletedIsFalseOrderByCreatedAtAsc(placeId: String): List<Mileage>
}

