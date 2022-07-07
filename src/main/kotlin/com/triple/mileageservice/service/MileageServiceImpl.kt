package com.triple.mileageservice.service

import com.triple.mileageservice.aop.TimeCheck
import com.triple.mileageservice.domain.Mileage
import com.triple.mileageservice.domain.MileageHistory
import com.triple.mileageservice.dto.ReviewEvent
import com.triple.mileageservice.dto.UserMileageResponse
import com.triple.mileageservice.repository.MileageHistoryRepository
import com.triple.mileageservice.repository.MileageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils
import java.util.*
import kotlin.math.abs

@Service
@Transactional(readOnly = true)
class MileageServiceImpl(
    val mileageRepository: MileageRepository,
    val mileageHistoryRepository: MileageHistoryRepository
) : MileageService {

    @Transactional
    @TimeCheck
    override fun add(event: ReviewEvent) {
        require(
            !mileageRepository.existsByPlaceIdAndReviewIdAndUserIdAndDeletedIsFalse(
                event.placeId,
                event.reviewId,
                event.userId
            )
        ) { "이미 적립된 마일리지 내역입니다." }

        val savedPlaceReviewCnt = mileageRepository.countByPlaceIdAndDeletedIsFalse(event.placeId)
        val point = getPoint(event) + if (savedPlaceReviewCnt != 0) 0 else 1

        val saveMileage = mileageRepository.save(
            event.let {
                Mileage(
                    userId = it.userId,
                    reviewId = it.reviewId,
                    placeId = it.placeId,
                    mileageId = UUID.randomUUID().toString(),
                    contentLength = it.content.length,
                    attachedPhotoCnt = it.attachedPhotoIds.size,
                    point = point
                )
            }
        )

        mileageRepository.flush()

        val curUserPoint = mileageHistoryRepository
            .findFirstAllByUserIdOrderByCreatedAtDesc(event.userId)?.curUserPoint ?: 0

        val mileageHistory = saveMileage.let {
            MileageHistory(
                mileageId = it.mileageId,
                reviewId = it.reviewId,
                placeId = it.placeId,
                userId = it.userId,
                state = "PLUS",
                point = saveMileage.point,
                description = "새로운 리뷰 작성",
                contentLength = it.contentLength,
                attachedPhotoCnt = it.attachedPhotoCnt,
                curUserPoint + it.point
            )
        }
        mileageHistoryRepository.save(mileageHistory)
    }

    @Transactional
    override fun modify(event: ReviewEvent) {
        val mileage = findByUserIdAndPlaceIdAndReviewIdAndDeletedIsFalse(event)
        val mileageListByPlaceID = mileageRepository.findAllByPlaceIdAndDeletedIsFalseOrderByCreatedAtAsc(event.placeId)
        val point = getPoint(event, mileage, mileageListByPlaceID)

        val curUserPoint = mileageHistoryRepository
            .findFirstAllByUserIdOrderByCreatedAtDesc(event.userId)?.curUserPoint ?: 0

        if (mileage.point != point) {
            val action = if (mileage.point > point) "MINUS" else "PLUS"
            val pointGap = abs(mileage.point - point)

            val mileageHistory = mileage.let {
                MileageHistory(
                    mileageId = it.mileageId,
                    reviewId = it.reviewId,
                    placeId = it.placeId,
                    userId = it.userId,
                    state = action,
                    point = pointGap,
                    description = "리뷰 수정",
                    contentLength = event.content.length,
                    attachedPhotoCnt = event.attachedPhotoIds.size,
                    curUserPoint + point - mileage.point
                )
            }
            mileageHistoryRepository.save(mileageHistory)
        }
        mileage.update(event.content.length, event.attachedPhotoIds.size, point)
    }

    @Transactional
    override fun delete(event: ReviewEvent) {
        val mileage = findByUserIdAndPlaceIdAndReviewIdAndDeletedIsFalse(event)
        mileage.delete()

        val curUserPoint = mileageHistoryRepository
            .findFirstAllByUserIdOrderByCreatedAtDesc(event.userId)?.curUserPoint ?: 0

        val mileageHistory = mileage.let {
            MileageHistory(
                mileageId = it.mileageId,
                reviewId = it.reviewId,
                placeId = it.placeId,
                userId = it.userId,
                state = "MINUS",
                point = mileage.point,
                description = "리뷰 삭제",
                contentLength = it.contentLength,
                attachedPhotoCnt = it.attachedPhotoCnt,
                curUserPoint - mileage.point
            )
        }

        mileageHistoryRepository.save(mileageHistory)
    }

    override fun getUserMileage(userId: String): UserMileageResponse {
        val curUserPoint = mileageHistoryRepository.findFirstAllByUserIdOrderByCreatedAtDesc(userId)?.curUserPoint ?: 0
        return UserMileageResponse(userId, curUserPoint)
    }

    private fun getPoint(event: ReviewEvent, mileage: Mileage, mileageListByPlaceID: List<Mileage>): Int {
        var point = 0

        if (mileageListByPlaceID.isEmpty() || mileageListByPlaceID[0].reviewId == mileage.reviewId) {
            point += 1
        }
        point += getPoint(event)
        return point
    }

    private fun findByUserIdAndPlaceIdAndReviewIdAndDeletedIsFalse(event: ReviewEvent) =
        (
            mileageRepository.findByPlaceIdAndReviewIdAndUserIdAndDeletedIsFalse(
                event.placeId,
                event.reviewId,
                event.userId
            ) ?: throw IllegalArgumentException("해당 리뷰에 마일리지 적립 내역이 없습니다.")
            )

    private fun getPoint(event: ReviewEvent): Int {
        var point = 0
        if (StringUtils.hasText(event.content)) {
            point += 1
        }
        if (event.attachedPhotoIds.isNotEmpty()) {
            point += 1
        }
        return point
    }
}
