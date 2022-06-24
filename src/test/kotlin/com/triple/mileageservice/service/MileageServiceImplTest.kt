package com.triple.mileageservice.service

import com.triple.mileageservice.createMileage
import com.triple.mileageservice.createReviewEvent
import com.triple.mileageservice.dto.Action
import com.triple.mileageservice.repository.MileageRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
internal class MileageServiceImplTest {
    @MockK
    private lateinit var mileageRepository: MileageRepository
    @InjectMockKs
    private lateinit var mileageService: MileageServiceImpl

    @Nested
    @DisplayName("리뷰 생성 이벤트가 들어왔을 경우")
    inner class Add{
        @Test
        fun `리뷰ID 플레이스ID 유저ID로 저장된 마일리지가 없으면 저장성공`(){
            val event = createReviewEvent(action = Action.ADD)
            val mileage = createMileage()

            every{ mileageRepository.existsByUserIdAndPlaceIdAndReviewIdAndDeletedIsFalse(any(), any(), any()) } returns false
            every{ mileageRepository.countByPlaceId(any()) } returns 0
            every{ mileageRepository.save(any()) } returns mileage

            assertDoesNotThrow { mileageService.add(event) }
            verify(exactly = 1) {mileageRepository.save(any())}
        }

        @Test
        fun `리뷰ID 플레이스ID 유저ID로 저장된 유저가 있으면 예외 발생`() {
            val event = createReviewEvent(action = Action.ADD)

            every{mileageRepository.existsByUserIdAndPlaceIdAndReviewIdAndDeletedIsFalse(any(), any(), any())} returns true

            assertThrows<IllegalStateException> { mileageService.add(event) }
            verify(inverse = true) {mileageRepository.countByPlaceId(any())}
            verify(inverse = true) {mileageRepository.save(any())}
        }

    }

}