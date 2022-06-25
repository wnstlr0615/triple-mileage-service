package com.triple.mileageservice.service

import com.triple.mileageservice.createMileage
import com.triple.mileageservice.createReviewEvent
import com.triple.mileageservice.dto.Action
import com.triple.mileageservice.dto.ReviewEvent
import com.triple.mileageservice.repository.MileageHistoryRepository
import com.triple.mileageservice.repository.MileageRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.stream.Stream

@SpringBootTest

@ActiveProfiles("test")
class MileageServiceImplBootTest {
    @Autowired
    lateinit var mileageService: MileageService
    @Autowired
    lateinit var mileageRepository: MileageRepository
    @Autowired
    lateinit var mileageHistoryRepository: MileageHistoryRepository

    @AfterEach
    fun tearDown() {
        mileageRepository.deleteAll()
        mileageHistoryRepository.deleteAll()
    }

    @ParameterizedTest
    @MethodSource(value = ["reviewEventProvider"])
    @DisplayName("[리뷰 추가 이벤트] -첫 리뷰인 경우 리뷰 내용과 사진 유무에 따른 포인트 부여 ")
    fun `첫 리뷰인 경우 리뷰 내용과 사진 유무에 따른 포인트 부여`(
        point: Int,
        event: ReviewEvent
    ) {
        mileageService.add(event)
        val findMileage =
            mileageRepository.findByPlaceIdAndReviewIdAndUserIdAndDeletedIsFalse(
                event.placeId,
                event.reviewId,
                event.userId
            )
        assertThat(findMileage!!.point).isEqualTo(point + 1)
    }
    @ParameterizedTest
    @MethodSource(value = ["reviewEventProvider"])
    @DisplayName("[리뷰 추가 이벤트] - 첫 리뷰가 아닌 경우 리뷰 내용과 사진 유무에 따른 포인트 부여 ")
    fun `첫 리뷰가 아닌 경우 리뷰 내용과 사진 유무에 따른 포인트 부여`(
        point: Int,
        event: ReviewEvent
    ) {
        mileageRepository.save(createMileage(reviewId = "review1", userId = "otherUser"))

        mileageService.add(event)
        val findMileage =
            mileageRepository.findByPlaceIdAndReviewIdAndUserIdAndDeletedIsFalse(
                event.placeId,
                event.reviewId,
                event.userId
            )

        assertThat(findMileage!!.point).isEqualTo(point)
    }
    @ParameterizedTest
    @MethodSource(value = ["reviewEventProvider"])
    fun `마일리지 적립 시 history 로그 확인`(
        point: Int,
        event: ReviewEvent
    ) {
        mileageService.add(event)
        val mileage =
            mileageRepository.findByPlaceIdAndReviewIdAndUserIdAndDeletedIsFalse(
                event.placeId,
                event.reviewId,
                event.userId
            ) ?: createMileage()
        val mileageHistory = mileageHistoryRepository.findByUserIdAndPlaceIdAndReviewIdOrderByCreatedAtDesc(
            mileage.userId,
            mileage.placeId,
            mileage.reviewId
        )[0]

        assertAll(
            { assertThat(mileageHistory.state).isEqualTo("PLUS") },
            { assertThat(mileageHistory.curUserPoint).isEqualTo(point + 1) },
            { assertThat(mileageHistory.point).isEqualTo(mileage.point) },
            { assertThat(mileageHistory.contentLength).isEqualTo(mileage.contentLength) },
            { assertThat(mileageHistory.attachedPhotoCnt).isEqualTo(mileage.attachedPhotoCnt) },
        )
    }
    @ParameterizedTest
    @MethodSource("reviewModifyEventProvider")
    fun `마일리지 수정 시 증감 여부 확인`(
        state: String,
        point: Int,
        curUserPoint: Int,
        event: ReviewEvent
    ) {
        mileageService.add(createReviewEvent(attachedPhotoIds = emptyList())) // 현재 포인트 2점인 상태(본문 내용 10자, 첨부 이미지 0개, 첫 리뷰)
        mileageService.modify(event)
        val mileageHistory = mileageHistoryRepository.findByUserIdAndPlaceIdAndReviewIdOrderByCreatedAtDesc(
            event.userId,
            event.placeId,
            event.reviewId
        )[0]
        assertAll(
            { assertThat(mileageHistory.state).isEqualTo(state) },
            { assertThat(mileageHistory.curUserPoint).isEqualTo(curUserPoint) },
            { assertThat(mileageHistory.point).isEqualTo(point) },

        )
    }

    companion object {
        @JvmStatic
        fun reviewEventProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.arguments(
                    2, createReviewEvent(action = Action.ADD, content = "좋아요!", attachedPhotoIds = listOf("aaa111", "bbb11"))
                ),
                Arguments.arguments(
                    1, createReviewEvent(action = Action.ADD, content = "좋아요!", attachedPhotoIds = emptyList())
                ),
                Arguments.arguments(
                    1, createReviewEvent(action = Action.ADD, content = "", attachedPhotoIds = listOf("aaa111", "bbb11"))
                ),
                Arguments.arguments(
                    0, createReviewEvent(action = Action.ADD, content = "", attachedPhotoIds = emptyList())
                )
            )
        }

        // state , point, curUserPoint, reviewEvent
        @JvmStatic
        fun reviewModifyEventProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.arguments(
                    "PLUS", 1, 3, createReviewEvent(action = Action.MOD, content = "좋아요!", attachedPhotoIds = listOf("aaa111", "bbb11"))
                ),
                Arguments.arguments(
                    "MINUS", 1, 1, createReviewEvent(action = Action.MOD, content = "", attachedPhotoIds = emptyList())
                )
            )
        }
    }
}
