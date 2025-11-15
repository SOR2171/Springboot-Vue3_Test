package com.github.sor2171.backend.utils

import jakarta.annotation.Resource
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class FlowUtils(
    @Resource
    val template: StringRedisTemplate
) {
    
    fun limitOnceCheck(key: String, blockTime: Int): Boolean {
        if (template.hasKey(key)) {
            return false
        } else {
            template.opsForValue().set(
                key,
                "",
                blockTime.toLong(),
                TimeUnit.SECONDS
            )
            return true
        }
    }
}