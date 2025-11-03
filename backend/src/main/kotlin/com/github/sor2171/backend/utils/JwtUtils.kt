package com.github.sor2171.backend.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.Calendar
import java.util.Date

@Component
class JwtUtils(
    @param:Value("\${spring.security.jwt.key}")
    val key: String,
    
    @param:Value("\${spring.security.jwt.expire-hours}")
    val expireHours: Int
) {

    fun createJwt(
        details: UserDetails,
        id: Int,
        username: String
    ): String {
        val algorithm = Algorithm.HMAC256(key)
        return JWT
            .create()
            .withClaim("id", id)
            .withClaim("username", username)
            .withClaim(
                "authorities",
                details.authorities.map { it.authority }.toList()
            )
            .withExpiresAt(expiresTime())
            .withIssuedAt(Date())
            .sign(algorithm)
    }
    
    private fun expiresTime(): Date {
        val cal = Calendar.getInstance()
        cal.add(Calendar.HOUR, expireHours)
        return cal.time
    }
}