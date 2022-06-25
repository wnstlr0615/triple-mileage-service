package com.triple.mileageservice.controller

import com.triple.mileageservice.dto.ReviewEvent
import com.triple.mileageservice.service.MileageService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class MileageController(
    private val mileageService: MileageService
) {
    @PostMapping("/events")
    fun reviewEvent(
        @Valid @RequestBody event: ReviewEvent
    ): ResponseEntity<Unit> {
        when (event.action) {
            "ADD" -> { mileageService.add(event) }
            "MOD" -> { mileageService.modify(event) }
            "DELETE" -> { mileageService.delete(event) }
        }
        return ResponseEntity.ok().build()
    }
    @GetMapping("/users/{userId}/mileage")
    fun getUserMileage(
        @PathVariable userId: String
    ): ResponseEntity<ApiResponse<*>> {
        return ResponseEntity.ok(
            ApiResponse.success(
                mileageService.getUserMileage(userId)
            )
        )
    }
}
