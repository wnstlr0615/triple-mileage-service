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
    fun `사용자 현재 포인트 조회 하기 `() {
        val userId = "userId"
        mileageHistoryRepository.save(
            createMileageHistory(userId = userId, reviewId = "reviewId1", placeId = "placeId1", action = "ADD", point = 3, curUserPoint = 3)
        )
        sleep(500)
        mileageHistoryRepository.save(
            createMileageHistory(userId = userId, reviewId = "reviewId2", placeId = "placeId2", action = "MINUS", point = 1, curUserPoint = 2),
        )
        sleep(500)
        mileageHistoryRepository.save(
            createMileageHistory(userId = userId, reviewId = "reviewId3", placeId = "placeId3", action = "ADD", point = 2, curUserPoint = 4),
        )
        sleep(500)
        mileageHistoryRepository.save(
            createMileageHistory(userId = userId, reviewId = "reviewId4", placeId = "placeId4", action = "ADD", point = 1, curUserPoint = 5),
        )
        sleep(500)
        mileageHistoryRepository.save(
            createMileageHistory(userId = userId, reviewId = "reviewId5", placeId = "placeId5", action = "ADD", point = 2, curUserPoint = 7)
        )

        assertThat(mileageHistoryRepository.findFirstAllByUserIdOrderByCreatedAtDesc(userId)?.curUserPoint).isEqualTo(7)
    }

    @Test
    fun `하나의 리뷰에 대한 마일리지 한 사용자 적립내역 조회하기`() {
        val userId = "userId"
        val placeId = "placeId1"
        val reviewId = "reviewId1"
        mileageHistoryRepository.saveAll(
            listOf(
                createMileageHistory(userId = userId, reviewId = reviewId, placeId = placeId, action = "ADD", point = 3, curUserPoint = 3),
                createMileageHistory(userId = userId, reviewId = reviewId, placeId = placeId, action = "MINUS", point = 1, curUserPoint = 2),
                createMileageHistory(userId = "otherUserId", reviewId = "reviewId2", placeId = placeId, action = "ADD", point = 3, curUserPoint = 3),
                createMileageHistory(userId = userId, reviewId = reviewId, placeId = placeId, action = "ADD", point = 1, curUserPoint = 3)
            )
        )

        val userMileageHistoryList =
            mileageHistoryRepository.findByUserIdAndPlaceIdAndReviewIdOrderByCreatedAtDesc(userId, placeId, reviewId)

        assertThat(userMileageHistoryList.size).isEqualTo(3)
    }
}
