package com.triple.mileageservice.repository

import com.triple.mileageservice.domain.MileageHistory
import org.springframework.data.jpa.repository.JpaRepository

interface MileageHistoryRepository : JpaRepository<MileageHistory, Long> {
    fun findFirstAllByUserIdOrderByCreatedAtDesc(userId: String): MileageHistory?
}