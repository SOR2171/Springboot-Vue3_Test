package com.github.sor2171.backend.filter

import com.github.sor2171.backend.utils.JwtUtils
import jakarta.annotation.Resource
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthorizeFilter : OncePerRequestFilter(
) {
    @Resource
    lateinit var utils: JwtUtils
    
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorization = request.getHeader(HttpHeaders.AUTHORIZATION)
        val jwt = utils.resolveJwt(authorization)
        if (jwt != null) {
            val user = utils.toUser(jwt)
            val authentication = UsernamePasswordAuthenticationToken(
                user,
                null,
                user.authorities
            )
            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authentication
            request.setAttribute("id", utils.toId(jwt))
        }
        filterChain.doFilter(request, response)
    }
}