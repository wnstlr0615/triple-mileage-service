package com.triple.mileageservice.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class ReviewEvent(
    @field:NotBlank(message = "type must not be blank")
    val type: String,

    @field:NotBlank(message = "action must not be blank")
    val action: String,

    @field:NotBlank(message = "reviewId must not be blank")
    val reviewId: String,

    @field:NotNull(message = "content must not be null")
    val content: String,

    @field:NotNull(message = "attachedPhotoIds must not null")
    val attachedPhotoIds: List<String>,

    @field:NotBlank(message = "userId must not be blank")
    val userId: String,

    @field:NotBlank(message = "placeId must not be blank")
    val placeId: String
)

enum class Action {
    ADD,
    MOD,
    DELETE
}
