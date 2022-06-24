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
            !mileageRepository.existsByUserIdAndPlaceIdAndReviewIdAndDeletedIsFalse
                    (event.userId, event.placeId, event.reviewId)
        ) {"이미 적립된 마일리지 내역입니다."}

        val savedPlaceReviewCnt = mileageRepository.countByPlaceIdAndDeletedIsFalse(event.placeId)
        val point = event.getPoint() + if(savedPlaceReviewCnt != 0) 0 else 1

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

    override fun modify(event: ReviewEvent) {
        TODO("리뷰 이벤트가 MOD 일 경우 마일리지 관련 비즈니스 로직 구현")
    }

    @Transactional
    override fun delete(event: ReviewEvent) {
        val mileage = findByUserIdAndPlaceIdAndReviewIdAndDeletedIsFalse(event)
        mileage.delete()
    }

    private fun findByUserIdAndPlaceIdAndReviewIdAndDeletedIsFalse(event: ReviewEvent) =
        (mileageRepository.findByUserIdAndPlaceIdAndReviewIdAndDeletedIsFalse(
            event.userId,
            event.placeId,
            event.reviewId
        ) ?: throw IllegalArgumentException("해당 리뷰에 마일리지 적립 내역이 없습니다."))

    private fun ReviewEvent.getPoint(): Int{
        var point = 0
        if(StringUtils.hasText(this.content)){
            point += 1
        }
        if(this.attachedPhotoIds.isNotEmpty()){
            point += 1
        }
        return point
    }
}
