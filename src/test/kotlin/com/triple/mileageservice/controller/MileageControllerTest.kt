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
    fun `????????? ?????? ?????? ???????????? ??? ?????? ?????? ???????????? ??????`() {
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
                        fieldWithPath("type").type(JsonFieldType.STRING).description("????????? ??????"),
                        fieldWithPath("action").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
                        fieldWithPath("reviewId").type(JsonFieldType.STRING).description("?????? Id"),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("attachedPhotoIds").type(JsonFieldType.ARRAY).description("????????? ????????? Id"),
                        fieldWithPath("userId").type(JsonFieldType.STRING).description("????????? ????????? ????????? Id"),
                        fieldWithPath("placeId").type(JsonFieldType.STRING).description("????????? ????????? ?????? Id"),
                    )
                )
            )
    }
    @Test
    fun `????????? ???????????? ??????`() {
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
                        parameterWithName("userId").description("????????? ID")
                    ),
                    responseFields(
                        fieldWithPath("message").description("?????? ??? ?????????"),
                        subsectionWithPath("body").description("??????"),
                        fieldWithPath("body.userId").type(JsonFieldType.STRING).description("????????? ID"),
                        fieldWithPath("body.mileage").type(JsonFieldType.NUMBER).description("????????? ?????? ????????????")
                    )
                )
            )
    }
}
