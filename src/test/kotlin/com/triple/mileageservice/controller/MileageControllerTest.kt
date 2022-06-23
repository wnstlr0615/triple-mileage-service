package com.triple.mileageservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.triple.mileageservice.config.RestDocsConfiguration
import com.triple.mileageservice.dto.ReviewEvent
import com.triple.mileageservice.service.MileageServiceImpl
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles("test")
@Import(RestDocsConfiguration::class)
@ExtendWith(value = [RestDocumentationExtension::class])
@WebMvcTest(controllers = [MileageController::class])
@AutoConfigureRestDocs
@AutoConfigureMockMvc(print =  MockMvcPrint.LOG_DEBUG)
internal class MileageControllerTest {
    @Autowired
    lateinit var mapper: ObjectMapper

    @Autowired
    lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var mileageService: MileageServiceImpl

    private val reviewEvent: ReviewEvent = ReviewEvent(
            type = "REVIEW",
            action = "ADD",
            reviewId = "240a0658-dc5f-4878-9381-ebb7b2667772",
            content = "좋아요!",
            attachedPhotoIds = listOf(
                "e4d1a64e-a531-46de-88d0-ff0ed70c0bb8",
                "afb0cef2-851d-4a50-bb07-9cc15cbdc332"
            ),
            userId = "3ede0ef2-92b7-4817-a5f3-0c575361f745",
            placeId = "2e4baf1c-5acb-4efb-a1af-eddada31b00f"
    )

    @Test
    fun `새로운 리뷰 작성 이벤트가 올 경우 신규 마일리지 적립`(){
        every { mileageService.add(reviewEvent) } just Runs
        mvc.perform(post("/events")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                mapper.writeValueAsString(
                    reviewEvent
                )
            )
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "review_event_add",
                    requestFields(
                        fieldWithPath("type").type(JsonFieldType.STRING).description("이벤트 타입"),
                        fieldWithPath("action").type(JsonFieldType.STRING).description("리뷰 액션 타입"),
                        fieldWithPath("reviewId").type(JsonFieldType.STRING).description("리뷰 Id"),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰 내용"),
                        fieldWithPath("attachedPhotoIds").type(JsonFieldType.ARRAY).description("첨부된 이미지 Id"),
                        fieldWithPath("userId").type(JsonFieldType.STRING).description("리뷰를 작성한 사용자 Id"),
                        fieldWithPath("placeId").type(JsonFieldType.STRING).description("리뷰를 작성한 장송 Id"),
                    )
                )
            )
    }
}