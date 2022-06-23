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
        check(
            !mileageRepository.existsByUserIdAndPlaceIdAndReviewIdAndDeletedIsFalse
                    (event.userId, event.placeId, event.reviewId)
        ) {"이미 적립된 마일리지 내역이 존재합니다."}

        val savedPlaceReviewCnt = mileageRepository.countByPlaceId(event.placeId)
        val point = event.getPoint() + if(savedPlaceReviewCnt > 0) 0 else 1
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

    override fun delete(event: ReviewEvent) {
        TODO("리뷰 이벤트가 DELETE 일 경우 마일리지 관련 비즈니스 로직 구현")
    }

    fun ReviewEvent.getPoint(): Int{
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
