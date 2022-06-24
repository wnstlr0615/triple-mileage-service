package com.triple.mileageservice.repository

import com.triple.mileageservice.domain.Mileage
import org.springframework.data.jpa.repository.JpaRepository

interface MileageRepository : JpaRepository<Mileage, Long> {
    fun countByPlaceIdAndDeletedIsFalse(placeId: String): Int
    fun existsByUserIdAndPlaceIdAndReviewIdAndDeletedIsFalse(userId: String, placeId: String, reviewId: String): Boolean
    fun findByUserIdAndPlaceIdAndReviewIdAndDeletedIsFalse(userId: String, placeId: String, reviewId: String): Mileage?
}

