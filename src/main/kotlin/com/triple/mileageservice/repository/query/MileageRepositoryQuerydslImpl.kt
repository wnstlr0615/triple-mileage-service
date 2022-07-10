package com.triple.mileageservice.repository.query

import com.querydsl.jpa.impl.JPAQueryFactory
import com.triple.mileageservice.domain.Mileage
import com.triple.mileageservice.domain.QMileage.mileage

class MileageRepositoryQuerydslImpl(
    private val queryFactory: JPAQueryFactory
) : MileageRepositoryQuerydsl {
    override fun findMileageUserId(userId: String): Mileage {
        return queryFactory.selectFrom(mileage)
            .where(mileage.userId.eq(userId))
            .fetchOne() ?: throw IllegalArgumentException("userId 로된 마일리지 내역을 찾지 못했습니다.")
    }
}
