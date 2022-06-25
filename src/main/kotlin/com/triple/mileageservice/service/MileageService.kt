package com.triple.mileageservice.service

import com.triple.mileageservice.dto.ReviewEvent
import com.triple.mileageservice.dto.UserMileageResponse

interface MileageService {
    fun add(event: ReviewEvent)
    fun modify(event: ReviewEvent)
    fun delete(event: ReviewEvent)
    fun getUserMileage(userId: String): UserMileageResponse
}
