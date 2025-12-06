package com.github.sor2171.backend.filter

import com.github.sor2171.backend.entity.RestBean
import com.github.sor2171.backend.utils.Const
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpFilter
import org.springframework.core.annotation.Order
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
@Order(Const.FLOW_LIMIT_ORDER)
class FlowLimitFilter(
    private val template: StringRedisTemplate
) : HttpFilter() {

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val address = request!!.remoteAddr
        if (this.tryCount(address)) {
            chain!!.doFilter(request, response)
        } else {
            this.writeBlockMessage(response!!)
        }
    }

    private fun writeBlockMessage(response: ServletResponse) {
        response.contentType = "application/json;charset=UTF-8"
        response.writer.write(RestBean.forbidden("Too many requests").toJsonString())
    }

    private fun tryCount(ip: String): Boolean {
        synchronized(ip.intern()) {
            if (template.hasKey(Const.FLOW_LIMIT_BLOCK + ip)) return false

            if (template.hasKey(Const.FLOW_LIMIT_COUNTER + ip)) {
                val increment = template.opsForValue().increment(Const.FLOW_LIMIT_COUNTER + ip) ?: 0
                if (increment > 10) {
                    template.opsForValue().set(
                        Const.FLOW_LIMIT_BLOCK + ip,
                        "1",
                        1,
                        TimeUnit.MINUTES
                    )
                    return false
                } else return true

            } else {
                template.opsForValue().set(
                    Const.FLOW_LIMIT_COUNTER + ip,
                    "1",
                    3,
                    TimeUnit.SECONDS
                )
                return true
            }
        }
    }
}