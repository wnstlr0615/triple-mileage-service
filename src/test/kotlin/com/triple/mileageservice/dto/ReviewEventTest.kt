package com.triple.mileageservice.dto

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.stream.Stream
import javax.validation.Validation

@SpringBootTest
@ActiveProfiles("test")
internal class ReviewEventTest {

    @ParameterizedTest
    @MethodSource("reviewEventValidateProvider")
    fun `BeanValidation 테스트`(
        size: Int,
        event: ReviewEvent
    ) {
        val validatorFactory = Validation.buildDefaultValidatorFactory()
        val validate = validatorFactory.validator.validate(event)

        assertThat(validate.size).isEqualTo(size)
    }

    companion object {
        @JvmStatic
        fun reviewEventValidateProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.arguments(
                    5,
                    ReviewEvent(
                        type = "",
                        action = "",
                        reviewId = "",
                        content = "",
                        attachedPhotoIds = emptyList(),
                        userId = "",
                        placeId = ""
                    ),
                ),
                Arguments.arguments(
                    4,
                    ReviewEvent(
                        type = "",
                        action = "ADD",
                        reviewId = "",
                        content = "좋아요",
                        attachedPhotoIds = listOf("aaa", "bbb"),
                        userId = "",
                        placeId = ""
                    ),
                ),
                Arguments.arguments(
                    3,
                    ReviewEvent(
                        type = "",
                        action = "",
                        reviewId = "",
                        content = "",
                        attachedPhotoIds = emptyList(),
                        userId = "bbb",
                        placeId = "aaa"
                    ),
                ),
                Arguments.arguments(
                    2,
                    ReviewEvent(
                        type = "REVIEW",
                        action = "ADD",
                        reviewId = "a",
                        content = "",
                        attachedPhotoIds = emptyList(),
                        userId = "",
                        placeId = ""
                    ),
                )
            )
        }
    }
}
