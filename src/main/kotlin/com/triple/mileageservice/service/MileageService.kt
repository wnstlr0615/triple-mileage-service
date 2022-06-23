package com.triple.mileageservice.service

import com.triple.mileageservice.dto.ReviewEvent

interface MileageService {
    fun add(event: ReviewEvent)
    fun modify(event: ReviewEvent)
    fun delete(event: ReviewEvent)
}
