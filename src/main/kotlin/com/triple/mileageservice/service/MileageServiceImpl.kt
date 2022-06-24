package com.triple.mileageservice.service

import com.triple.mileageservice.domain.Mileage
import com.triple.mileageservice.dto.ReviewEvent
import com.triple.mileageservice.repository.MileageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils
import java.util.*

@Service
@Transactional(readOnly = true)
class MileageServiceImpl(
    val mileageRepository: MileageRepository
) : MileageService {

    @Transactional
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

        mileageRepository.save(
            event.let {
                Mileage(
                    it.userId,
                    it.reviewId,
                    it.placeId,
                    UUID.randomUUID().toString(),
                    it.attachedPhotoIds.size,
                    point
                )
            }
        )
    }

    @Transactional
    override fun modify(event: ReviewEvent) {
        val mileage = findByUserIdAndPlaceIdAndReviewIdAndDeletedIsFalse(event)
        val mileageListByPlaceID = mileageRepository.findAllByPlaceIdAndDeletedIsFalseOrderByCreatedAtAsc(event.placeId)
        val point = getPoint(event, mileage, mileageListByPlaceID)

        mileage.update(event.attachedPhotoIds.size, point)
    }

    private fun getPoint(event: ReviewEvent, mileage: Mileage, mileageListByPlaceID: List<Mileage>): Int {
        var point = 0

        if (mileageListByPlaceID.isEmpty() || mileageListByPlaceID[0].reviewId == mileage.reviewId) {
            point += 1
        }
        point += getPoint(event)
        return point
    }

    @Transactional
    override fun delete(event: ReviewEvent) {
        val mileage = findByUserIdAndPlaceIdAndReviewIdAndDeletedIsFalse(event)
        mileage.delete()
    }

    private fun findByUserIdAndPlaceIdAndReviewIdAndDeletedIsFalse(event: ReviewEvent) =
        (mileageRepository.findByPlaceIdAndReviewIdAndUserIdAndDeletedIsFalse(
            event.placeId,
            event.reviewId,
            event.userId
        ) ?: throw IllegalArgumentException("해당 리뷰에 마일리지 적립 내역이 없습니다."))

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
