package com.triple.mileageservice.repository.query

import com.triple.mileageservice.domain.Mileage

interface MileageRepositoryQuerydsl {
    fun findMileageUserId(userId: String): Mileage
}
