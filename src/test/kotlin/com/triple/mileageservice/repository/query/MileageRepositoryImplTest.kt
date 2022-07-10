package com.triple.mileageservice.repository.query

import com.triple.mileageservice.createMileage
import com.triple.mileageservice.repository.MileageRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@Transactional
internal class MileageRepositoryImplTest {
    @Autowired
    lateinit var mileageRepository: MileageRepository

    @Test
    fun `유저 아이디를 통한 마일리지 내역 조회`() {
        val userId = UUID.randomUUID().toString()
        mileageRepository.save(createMileage(userId = userId))
        mileageRepository.save(createMileage(userId = UUID.randomUUID().toString()))
        mileageRepository.save(createMileage(userId = UUID.randomUUID().toString()))
        mileageRepository.save(createMileage(userId = UUID.randomUUID().toString()))
        mileageRepository.save(createMileage(userId = UUID.randomUUID().toString()))

        val mileage = mileageRepository.findMileageUserId(userId)

        assertThat(mileage.userId).isEqualTo(userId)
    }
}
