package com.triple.mileageservice.repository

import com.triple.mileageservice.createMileageHistory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.lang.Thread.sleep

@DataJpaTest
@ActiveProfiles("test")
class MileageHistoryRepositoryTest {
    @Autowired
    lateinit var mileageHistoryRepository: MileageHistoryRepository

    @Test
    fun `사용자 현재 포인트 조회 하기 `(){
        val userId = "userId"
        mileageHistoryRepository.save(
            createMileageHistory(userId = userId, reviewId = "reviewId1" , placeId = "placeId1", action = "ADD", point = 3, userCurrentPoint = 3)
        )
        sleep(500)
        mileageHistoryRepository.save(
            createMileageHistory(userId = userId, reviewId = "reviewId2" , placeId = "placeId2", action = "MINUS", point = 1, userCurrentPoint = 2),
        )
        sleep(500)
        mileageHistoryRepository.save(
            createMileageHistory(userId = userId, reviewId = "reviewId3" , placeId = "placeId3", action = "ADD", point = 2, userCurrentPoint = 4),
        )
        sleep(500)
        mileageHistoryRepository.save(
            createMileageHistory(userId = userId, reviewId = "reviewId4" , placeId = "placeId4", action = "ADD", point = 1, userCurrentPoint = 5),
        )
        sleep(500)
        mileageHistoryRepository.save(
            createMileageHistory(userId = userId, reviewId = "reviewId5" , placeId = "placeId5", action = "ADD", point = 2, userCurrentPoint = 7)
        )

        assertThat(mileageHistoryRepository.findFirstAllByUserIdOrderByCreatedAtDesc(userId)?.userCurrentPoint).isEqualTo(7)
    }
}