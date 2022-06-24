package com.triple.mileageservice.service

import com.triple.mileageservice.createMileage
import com.triple.mileageservice.createReviewEvent
import com.triple.mileageservice.dto.Action
import com.triple.mileageservice.dto.ReviewEvent
import com.triple.mileageservice.repository.MileageRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.stream.Stream

@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
internal class MileageServiceImplTest {
    @MockK
    private lateinit var mileageRepository: MileageRepository
    @InjectMockKs
    private lateinit var mileageService: MileageServiceImpl

    @Test
    @DisplayName("[리뷰 생성 이벤트] - 리뷰ID 플레이스ID 유저ID로 저장된 마일리지가 없으면 저장성공")
    fun `리뷰ID 플레이스ID 유저ID로 저장된 마일리지가 없으면 저장성공`(){
        val event = createReviewEvent(action = Action.ADD)
        val mileage = createMileage()

        every{ mileageRepository.existsByPlaceIdAndReviewIdAndUserIdAndDeletedIsFalse(any(), any(), any()) } returns false
        every{ mileageRepository.countByPlaceIdAndDeletedIsFalse(any()) } returns 0
        every{ mileageRepository.save(any()) } returns mileage

        assertDoesNotThrow { mileageService.add(event) }
        verify(exactly = 1) {mileageRepository.save(any())}
    }
    @Test
    @DisplayName("[리뷰 생성 이벤트] - 리뷰ID 플레이스ID 유저ID로 저장된 유저가 있으면 예외 발생")
    fun `리뷰ID 플레이스ID 유저ID로 저장된 유저가 있으면 예외 발생`() {
        val event = createReviewEvent(action = Action.ADD)

        every{mileageRepository.existsByPlaceIdAndReviewIdAndUserIdAndDeletedIsFalse(any(), any(), any())} returns true

        assertThrows<IllegalArgumentException> { mileageService.add(event) }
        verify(inverse = true) {mileageRepository.countByPlaceIdAndDeletedIsFalse(any())}
        verify(inverse = true) {mileageRepository.save(any())}
    }

    @ParameterizedTest
    @MethodSource(value = ["reviewEventProvider"])
    @DisplayName("[리뷰 수정 이벤트] - 리뷰 수정 시 해당 장소에 첫 리뷰이기 때문에 보너스 포인트를 1점을 추가로 받는 경우")
    fun `리뷰 수정 시 해당 장소에 첫 리뷰이기 때문에 보너스 포인트를 1점을 추가로 받는 경우 `(
        point: Int,
        event: ReviewEvent
    ){
        val placeId = "2e4baf1c-5acb-4efb-a1af-eddada31b00f"
        val userId = "user1"
        val reviewId = "review1"
        val mileage = createMileage(placeId = placeId, userId = userId, reviewId = reviewId, attachedPhotoCnt = 2, point = 3)

        every {
            mileageRepository.findByPlaceIdAndReviewIdAndUserIdAndDeletedIsFalse(any(), any(), any())
        } returns mileage
        every {
            mileageRepository.findAllByPlaceIdAndDeletedIsFalseOrderByCreatedAtAsc(any())
        } returns listOf(
            createMileage(placeId = placeId, userId = userId, reviewId = "review1", attachedPhotoCnt = 2, point = 3),
            createMileage(placeId = placeId, userId = "user2", reviewId = "review2", attachedPhotoCnt = 3, point = 2),
            createMileage(placeId = placeId, userId = "user3", reviewId = "review3", attachedPhotoCnt = 1, point = 2),
            createMileage(placeId = placeId, userId = "user4", reviewId = "review4", attachedPhotoCnt = 0, point = 1)
        )
        assertAll(
            { assertDoesNotThrow { mileageService.modify(event) } },
            { assertThat(mileage.point).isEqualTo(point + 1) },
            { assertThat(mileage.contentLength).isEqualTo(event.content.length) }
        )

    }

    @ParameterizedTest
    @MethodSource(value = ["reviewEventProvider"])
    @DisplayName("[리뷰 수정 이벤트] - 리뷰 수정 시 해당 리뷰가 첫번 쨰가 아니기 때문에 보너스 포인트를 받지 못하는 경우")
    fun `리뷰 수정 시 해당 리뷰가 첫번 쨰가 아니기 때문에 보너스 포인트를 받지 못하는 경우 `(
        point: Int,
        event: ReviewEvent
    ){
        val placeId = "2e4baf1c-5acb-4efb-a1af-eddada31b00f"
        val userId = "user1"
        val reviewId = "review1"
        val mileage = createMileage(placeId = placeId, userId = userId, reviewId = reviewId, attachedPhotoCnt = 2, point = 2)

        every {
            mileageRepository.findByPlaceIdAndReviewIdAndUserIdAndDeletedIsFalse(any(), any(), any())
        } returns mileage
        every {
            mileageRepository.findAllByPlaceIdAndDeletedIsFalseOrderByCreatedAtAsc(any())
        } returns listOf(
            createMileage(placeId = placeId, userId = "user2", reviewId = "review2", attachedPhotoCnt = 3, point = 3),
            createMileage(placeId = placeId, userId = userId, reviewId = "review1", attachedPhotoCnt = 2, point = 2),
            createMileage(placeId = placeId, userId = "user3", reviewId = "review3", attachedPhotoCnt = 1, point = 2),
            createMileage(placeId = placeId, userId = "user4", reviewId = "review4", attachedPhotoCnt = 0, point = 1)
        )
        assertAll(
            { assertDoesNotThrow { mileageService.modify(event) } },
            { assertThat(mileage.point).isEqualTo(point) },
            { assertThat(mileage.contentLength).isEqualTo(event.content.length) }
        )
    }
    @Test
    @DisplayName("[리뷰 수정 이벤트] - 해당 이벤트에 해당하는 마일리지 적립내역이 없는 경우 예외 발생")
    fun `해당 이벤트에 해당하는 마일리지 적립내역이 없는 경우 예외 발생`() {
        every {
            mileageRepository.findByPlaceIdAndReviewIdAndUserIdAndDeletedIsFalse(any(), any(), any())
        } returns null

        assertThrows<IllegalArgumentException> { mileageService.modify(createReviewEvent(action = Action.MOD))}
        verify(inverse = true) {mileageRepository.findAllByPlaceIdAndDeletedIsFalseOrderByCreatedAtAsc(any())}
    }



    @Test
    @DisplayName("[리뷰 삭제 이벤트] - 리뷰가 삭제되어 기존 마일리지 적립내역 flag 처리")
    fun `리뷰가 삭제되어 기존 마일리지 적립내역 flag 처리`(){
        val mileage = createMileage(deleted = false)

        every { mileageRepository.findByPlaceIdAndReviewIdAndUserIdAndDeletedIsFalse(any(), any(), any()) } returns mileage
        assertAll(
            { assertDoesNotThrow { mileageService.delete(createReviewEvent(action = Action.DELETE)) }},
            { assertThat(mileage.deleted).isTrue },
            { assertThat(mileage.deletedAt).isNotNull }
        )
        verify(exactly = 1) {mileageRepository.findByPlaceIdAndReviewIdAndUserIdAndDeletedIsFalse(any(), any(), any())}
    }

    companion object{
        @JvmStatic
        fun reviewEventProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.arguments(2,
                    createReviewEvent(action = Action.MOD, content = "좋아요!", attachedPhotoIds = listOf("aaa111", "bbb11"))),
                Arguments.arguments(1,
                    createReviewEvent(action = Action.MOD, content = "좋아요!", attachedPhotoIds = emptyList())),
                Arguments.arguments(1,
                    createReviewEvent(action = Action.MOD, content = "", attachedPhotoIds = listOf("aaa111", "bbb11"))),
                Arguments.arguments(0,
                    createReviewEvent(action = Action.MOD, content = "", attachedPhotoIds = emptyList()))
            )
        }
    }
}