package com.github.sor2171.backend.filter

import com.github.sor2171.backend.utils.Const
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component

@Component
@Order(Const.CORS_ORDER)
class CorsFilter : HttpFilter() {
    override fun doFilter(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        chain: FilterChain?
    ) {
        this.addCorsHeader(request, response)
        chain?.doFilter(request, response)
    }

    private fun addCorsHeader(
        request: HttpServletRequest?,
        response: HttpServletResponse?
    ) {
        response?.addHeader(
            HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
            request?.getHeader(HttpHeaders.ORIGIN)
        )
        response?.addHeader(
            HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
            "GET, POST, PUT, DELETE, OPTIONS"
        )
        response?.addHeader(
            HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
            "Authorization, Content-Type"
        )
    }
}