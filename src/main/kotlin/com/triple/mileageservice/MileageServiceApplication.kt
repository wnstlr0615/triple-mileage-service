package com.triple.mileageservice

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableBatchProcessing
class MileageServiceApplication

fun main(args: Array<String>) {
    runApplication<MileageServiceApplication>(*args)
}
