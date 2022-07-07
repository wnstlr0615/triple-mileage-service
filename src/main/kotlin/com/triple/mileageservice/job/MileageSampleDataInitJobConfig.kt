package com.triple.mileageservice.job

import com.triple.mileageservice.domain.Mileage
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder
import org.springframework.batch.item.support.ListItemReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*
import javax.persistence.EntityManagerFactory
import kotlin.random.Random

@Configuration
class MileageSampleDataInitJobConfig(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    val entityManagerFactory: EntityManagerFactory
) {
    @Bean
    fun sampleDataInitJob(): Job {
        return jobBuilderFactory.get("sampleDataInitJob")
            .incrementer(RunIdIncrementer())
            .start(sampleDataInitStep())
            .build()
    }

    @Bean
    @JobScope
    fun sampleDataInitStep(): Step {
        return stepBuilderFactory.get("sampleDataInitStep")
            .chunk<Mileage, Mileage>(1000)
            .reader(getMileageReader())
            .writer(jpaMileageWriter())
            .build()
    }

    private fun jpaMileageWriter(): ItemWriter<in Mileage> {
        val jpaItemWriter = JpaItemWriterBuilder<Mileage>()
            .entityManagerFactory(entityManagerFactory)
            .build()
        jpaItemWriter.afterPropertiesSet()
        return jpaItemWriter
    }

    private fun getPoint(contentLength: Int, attachedPhotoCnt: Int): Int {
        var point = 0
        if (contentLength > 0) {
            point++
        }
        if (attachedPhotoCnt > 0) {
            point++
        }
        return point
    }

    private fun getMileageReader(): ItemReader<out Mileage> {
        val mileages = mutableListOf<Mileage>()
        for (i in 0..1000000) {
            val userId = UUID.randomUUID()
            val reviewId = UUID.randomUUID()
            val placeId = UUID.randomUUID()
            val randomUUID = UUID.randomUUID()
            val contentLength = Random.nextInt(100)
            val attachedPhotoCnt = Random.nextInt(3)

            val point = getPoint(contentLength, attachedPhotoCnt)
            mileages.add(
                Mileage(
                    userId = userId.toString(),
                    reviewId = reviewId.toString(),
                    placeId = placeId.toString(),
                    mileageId = randomUUID.toString(),
                    contentLength = contentLength,
                    attachedPhotoCnt = attachedPhotoCnt,
                    point = point
                )
            )
        }
        println("job start")
        return ListItemReader(mileages)
    }
}
