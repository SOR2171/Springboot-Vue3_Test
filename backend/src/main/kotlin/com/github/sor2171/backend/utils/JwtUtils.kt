package com.github.sor2171.backend.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtils(
    @param:Value("\${spring.security.jwt.key}")
    val key: String,

    @param:Value("\${spring.security.jwt.expire-hours}")
    val expireHours: Int
) {
    fun resolveJwt(headerToken: String?): DecodedJWT? {
        val token = headerToken?.replace("Bearer ", "") ?: ""
        if (token.isBlank()) {
            return null
        } else {
            val algorithm = Algorithm.HMAC256(key)
            val verifier = JWT.require(algorithm).build()
            try {
                val decodedJWT = verifier.verify(token)
                val expiresAt = decodedJWT.expiresAt
                return if (Date().after(expiresAt)) {
                    null
                } else {
                    decodedJWT
                }
            } catch (e: Exception) {
                return null
            }
        }
    }

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

    fun toUser(jwt: DecodedJWT): UserDetails {
        val claims = jwt.claims
        return User
            .withUsername(claims["username"]!!.asString())
            .password("********")
            .authorities(*(claims["authorities"]!!.asArray(String::class.java)))
            .build()
    }
    
    fun toId(jwt: DecodedJWT) = jwt.claims["id"]?.asInt() ?: -1

    fun expiresTime(): Date {
        val cal = Calendar.getInstance()
        cal.add(Calendar.HOUR, expireHours)
        return cal.time
    }
}