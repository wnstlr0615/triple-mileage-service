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
    name = "MILEAGE",
    indexes = [
        Index(name = "place_index", columnList = "PLACE_ID"),
        Index(name = "place_review_user_index", columnList = "PLACE_ID, REVIEW_ID, USER_ID"),
    ]
)
class Mileage(
    @Column(name = "USER_ID", nullable = false)
    val userId: String,

    @Column(name = "REVIEW_ID", nullable = false)
    val reviewId: String,

    @Column(name = "PLACE_ID", nullable = false)
    val placeId: String,

    @Column(name = "MILEAGE_ID", nullable = false)
    val mileageId: String,

    @Column(name = "CONTENT_LENGTH", nullable = false)
    var contentLength: Int = 0,

    @Column(name = "ATTACHED_PHOTO_CNT", nullable = false)
    var attachedPhotoCnt: Int = 0,

    @Column(name = "POINT", nullable = false)
    var point: Int = 0,

    @Column(name = "DELETED", nullable = false)
    var deleted: Boolean = false,

    @Column(name = "DELETED_AT", nullable = true)
    var deletedAt: LocalDateTime? = null,

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "UPDATED_AT", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun delete() {
        deleted = true
        deletedAt = LocalDateTime.now()
    }

    fun update(contentLength: Int, attachedPhotoCnt: Int, point: Int) {
        this.contentLength = contentLength
        this.attachedPhotoCnt = attachedPhotoCnt
        this.point = point
        updatedAt = LocalDateTime.now()
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}
