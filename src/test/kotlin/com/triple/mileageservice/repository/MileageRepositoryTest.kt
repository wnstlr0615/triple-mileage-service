package com.triple.mileageservice.repository

import com.triple.mileageservice.createMileage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
internal class MileageRepositoryTest {
    @Autowired
    lateinit var mileageRepository: MileageRepository

    @Test
    fun `장소 ID를 가지는 마일리지 적립이 있는지 조회`() {
        val placeId = "2e4baf1c-5acb-4efb-a1af-eddada31b00f"

        assertThat(mileageRepository.countByPlaceId(placeId)).isEqualTo(0)

        mileageRepository.save(createMileage(placeId = placeId))

        assertThat(mileageRepository.countByPlaceId(placeId)).isEqualTo(1)
    }

    @CsvSource("false, true", "true, false")
    @ParameterizedTest
    fun `유저ID, 장소ID, 리뷰 ID를 통해 마일리지 적립 유무 확인`(deleted: Boolean, answer: Boolean){
        val reviewId = "240a0658-dc5f-4878-9381-ebb7b2667772"
        val userId = "3ede0ef2-92b7-4817-a5f3-0c575361f745"
        val placeId = "2e4baf1c-5acb-4efb-a1af-eddada31b00f"

        mileageRepository.save(createMileage(reviewId = reviewId, userId = userId, placeId = placeId, deleted = deleted))

        val isExist =
            mileageRepository.existsByUserIdAndPlaceIdAndReviewIdAndDeletedIsFalse(userId, placeId, reviewId)
        assertThat(isExist).isEqualTo(answer)
    }
}