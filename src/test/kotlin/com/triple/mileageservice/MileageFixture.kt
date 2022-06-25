package com.triple.mileageservice

import com.triple.mileageservice.domain.Mileage
import com.triple.mileageservice.domain.MileageHistory

fun createMileage(
    reviewId: String = "240a0658-dc5f-4878-9381-ebb7b2667772",
    userId: String = "3ede0ef2-92b7-4817-a5f3-0c575361f745",
    placeId: String = "2e4baf1c-5acb-4efb-a1af-eddada31b00f",
    mileageId: String = "c8200b29-0979-4d3c-8a13-425a74acc998",
    attachedPhotoCnt: Int = 0,
    contentLength: Int = 10,
    point: Int = 0,
    deleted: Boolean = false
): Mileage {
    return Mileage(
        userId = userId,
        reviewId = reviewId,
        placeId = placeId,
        mileageId = mileageId,
        contentLength = contentLength,
        attachedPhotoCnt = attachedPhotoCnt,
        point = point,
        deleted = deleted)
}

fun createMileageHistory(
    reviewId: String = "240a0658-dc5f-4878-9381-ebb7b2667772",
    userId: String = "3ede0ef2-92b7-4817-a5f3-0c575361f745",
    placeId: String = "2e4baf1c-5acb-4efb-a1af-eddada31b00f",
    mileageId: String = "c8200b29-0979-4d3c-8a13-425a74acc998",
    action: String = "ADD",
    attachedPhotoCnt: Int = 0,
    contentLength: Int = 10,
    point: Int = 0,
    description: String = "",
    userCurrentPoint: Int = 0
): MileageHistory {
    return MileageHistory(
        mileageId = mileageId,
        reviewId = reviewId,
        placeId = placeId,
        userId = userId,
        action = action,
        point = point,
        contentLength = contentLength,
        attachedPhotoCnt = attachedPhotoCnt,
        description = description,
        userCurrentPoint = userCurrentPoint
    )
}