package com.triple.mileageservice.repository

import com.triple.mileageservice.domain.Mileage
import com.triple.mileageservice.repository.query.MileageRepositoryQuerydsl
import org.springframework.data.jpa.repository.JpaRepository

interface MileageRepository : JpaRepository<Mileage, Long>, MileageRepositoryQuerydsl {
    fun countByPlaceIdAndDeletedIsFalse(placeId: String): Int
    fun existsByPlaceIdAndReviewIdAndUserIdAndDeletedIsFalse(placeId: String, reviewId: String, userId: String): Boolean
    fun findByPlaceIdAndReviewIdAndUserIdAndDeletedIsFalse(placeId: String, reviewId: String, userId: String): Mileage?
    fun findAllByPlaceIdAndDeletedIsFalseOrderByCreatedAtAsc(placeId: String): List<Mileage>
}
