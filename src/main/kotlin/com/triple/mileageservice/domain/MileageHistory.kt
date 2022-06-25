package com.triple.mileageservice.domain

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table

@Entity
@Table(
    name = "MILEAGE_HISTORY",
    indexes = [Index(name = "USER_ID_INDEX", columnList = "USER_ID")]
)
class MileageHistory(
    @Column(name = "MILEAGE_ID", nullable = false)
    val mileageId: String,

    @Column(name = "REVIEW_ID", nullable = false)
    val reviewId: String,

    @Column(name = "PLACE_ID", nullable = false)
    val placeId: String,

    @Column(name = "USER_ID", nullable = false)
    val userId: String,

    @Column(name = "ACTION", nullable = false)
    val action: String,

    @Column(name = "POINT", nullable = false)
    val point: Int,

    @Column(name = "DESCRIPTION", nullable = false)
    val description: String,

    @Column(name = "CONTENT_LENGTH", nullable = false)
    val contentLength: Int,

    @Column(name = "ATTACHED_PHOTO_CNT", nullable = false)
    val attachedPhotoCnt: Int,

    @Column(name = "USER_CURRENT_POINT", nullable = false)
    val userCurrentPoint: Int,

    @Column(name = "CREATED_AT", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}