package com.triple.mileageservice.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.StopWatch

@Component
@Aspect
class TimeCheckAop {

    @Around("@annotation(TimeCheck)")
    fun timeCheck(joinPoint: ProceedingJoinPoint): Any? {
        val stopWatch = StopWatch()
        stopWatch.start()
        val proceed = joinPoint.proceed()
        stopWatch.stop()
        log.info("spent time : {}", stopWatch.totalTimeSeconds)
        return proceed
    }
    companion object {
        val log = LoggerFactory.getLogger(TimeCheckAop::class.java)
    }
}
