package com.triple.mileageservice

import com.triple.mileageservice.dto.Action
import com.triple.mileageservice.dto.ReviewEvent

fun createReviewEvent(
    type: String = "REVIEW",
    action: Action = Action.ADD,
    reviewId: String = "240a0658-dc5f-4878-9381-ebb7b2667772",
    content: String = "좋아요",
    userId: String = "3ede0ef2-92b7-4817-a5f3-0c575361f745",
    placeId: String = "2e4baf1c-5acb-4efb-a1af-eddada31b00f",
    attachedPhotoIds: List<String> = listOf(
        "e4d1a64e-a531-46de-88d0-ff0ed70c0bb8",
        "afb0cef2-851d-4a50-bb07-9cc15cbdc332"
    )
): ReviewEvent {
    return ReviewEvent(
        type = type,
        action = action.name,
        reviewId = reviewId,
        content = content,
        userId = userId,
        placeId = placeId,
        attachedPhotoIds = attachedPhotoIds
    )
}
