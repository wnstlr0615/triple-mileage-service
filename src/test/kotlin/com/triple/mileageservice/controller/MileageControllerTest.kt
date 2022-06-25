package com.triple.mileageservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.triple.mileageservice.config.RestDocsConfiguration
import com.triple.mileageservice.createReviewEvent
import com.triple.mileageservice.createUserMileageResponse
import com.triple.mileageservice.dto.Action
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
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
@AutoConfigureMockMvc(print = MockMvcPrint.LOG_DEBUG)
internal class MileageControllerTest {
    @Autowired
    lateinit var mapper: ObjectMapper

    @Autowired
    lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var mileageService: MileageServiceImpl

    @Test
    fun `새로운 리뷰 작성 이벤트가 올 경우 신규 마일리지 적립`() {
        val reviewEvent = createReviewEvent(action = Action.ADD)

        every { mileageService.add(reviewEvent) } just Runs

        mvc.perform(
            post("/events")
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
                    "review_event",
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
    @Test
    fun `사용자 마일리지 조회`() {
        val userId = "3ede0ef2-92b7-4817-a5f3-0c575361f745"

        every { mileageService.getUserMileage(any()) } returns createUserMileageResponse(userId, 10)
        mvc.perform(
            RestDocumentationRequestBuilders.get("/users/{userId}/mileage", userId)
        ).andDo(print())
            .andExpect { status().isOk }
            .andDo(
                document(
                    "get_user_mileage",
                    pathParameters(
                        parameterWithName("userId").description("사용자 ID")
                    ),
                    responseFields(
                        fieldWithPath("message").description("에러 시 메시지"),
                        subsectionWithPath("body").description("응답"),
                        fieldWithPath("body.userId").type(JsonFieldType.STRING).description("사용자 ID"),
                        fieldWithPath("body.mileage").type(JsonFieldType.NUMBER).description("사용자 현재 마일리지")
                    )
                )
            )
    }
}
