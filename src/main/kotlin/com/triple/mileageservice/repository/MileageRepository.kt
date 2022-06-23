package com.triple.mileageservice.repository

import com.triple.mileageservice.domain.Mileage
import org.springframework.data.jpa.repository.JpaRepository

interface MileageRepository : JpaRepository<Mileage, Long> {
    fun countByPlaceId(placeId: String): Int
    fun existsByUserIdAndPlaceIdAndReviewIdAndDeletedIsFalse(userId: String, placeId: String, reviewId: String): Boolean
}

