package com.triple.mileageservice.repository

import com.triple.mileageservice.createMileage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.lang.Thread.sleep

@DataJpaTest
@ActiveProfiles("test")
internal class MileageRepositoryTest {
    @Autowired
    lateinit var mileageRepository: MileageRepository

    @Test
    fun `장소 ID를 가지는 마일리지 적립이 있는지 조회`() {
        val placeId = "2e4baf1c-5acb-4efb-a1af-eddada31b00f"

        assertThat(mileageRepository.countByPlaceIdAndDeletedIsFalse(placeId)).isEqualTo(0)

        mileageRepository.save(createMileage(placeId = placeId))
        mileageRepository.save(createMileage(placeId = placeId, deleted = true))

        assertThat(mileageRepository.countByPlaceIdAndDeletedIsFalse(placeId)).isEqualTo(1)
    }

    @CsvSource("false, true", "true, false")
    @ParameterizedTest
    fun `유저ID, 장소ID, 리뷰 ID를 통해 마일리지 적립 유무 확인`(deleted: Boolean, answer: Boolean) {
        val reviewId = "240a0658-dc5f-4878-9381-ebb7b2667772"
        val userId = "3ede0ef2-92b7-4817-a5f3-0c575361f745"
        val placeId = "2e4baf1c-5acb-4efb-a1af-eddada31b00f"

        mileageRepository.save(createMileage(reviewId = reviewId, userId = userId, placeId = placeId, deleted = deleted))

        val isExist =
            mileageRepository.existsByPlaceIdAndReviewIdAndUserIdAndDeletedIsFalse(placeId, reviewId, userId)
        assertThat(isExist).isEqualTo(answer)
    }

    @Test
    fun `유저ID, 장소ID, 리뷰 ID를 가진 마일리지 조회`() {
        val reviewId = "240a0658-dc5f-4878-9381-ebb7b2667772"
        val userId = "3ede0ef2-92b7-4817-a5f3-0c575361f745"
        val placeId = "2e4baf1c-5acb-4efb-a1af-eddada31b00f"

        assertThat(mileageRepository.findByPlaceIdAndReviewIdAndUserIdAndDeletedIsFalse(placeId, reviewId, userId)).isNull()

        mileageRepository.save(createMileage(reviewId = reviewId, userId = userId, placeId = placeId, deleted = false))

        assertThat(mileageRepository.findByPlaceIdAndReviewIdAndUserIdAndDeletedIsFalse(placeId, reviewId, userId)).isNotNull
    }

    @Test
    fun `장소ID로 등록된 마일리지 내역을 먼저 등록된 순으로 조회`() {
        val placeId = "2e4baf1c-5acb-4efb-a1af-eddada31b00f"

        mileageRepository.save(createMileage(userId = "user1", placeId = placeId, deleted = true))
        sleep(100)
        mileageRepository.save(createMileage(userId = "user2", placeId = placeId, deleted = false))
        sleep(100)
        mileageRepository.save(createMileage(userId = "user3", placeId = placeId, deleted = false))
        sleep(100)
        mileageRepository.save(createMileage(userId = "user4", placeId = placeId, deleted = false))

        val mileageList = mileageRepository.findAllByPlaceIdAndDeletedIsFalseOrderByCreatedAtAsc(placeId)

        assertAll(
            { assertThat(mileageList.size).isEqualTo(3) },
            { assertThat(mileageList[0].createdAt.isBefore(mileageList[1].createdAt)).isTrue() },
            { assertThat(mileageList[1].createdAt.isBefore(mileageList[2].createdAt)).isTrue() }
        )
    }
}
