package com.triple.mileageservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MileageServiceApplication

fun main(args: Array<String>) {
    runApplication<MileageServiceApplication>(*args)
}
