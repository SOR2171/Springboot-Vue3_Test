package com.github.sor2171.backend.controller

import com.github.sor2171.backend.entity.RestBean
import com.github.sor2171.backend.service.AccountService
import jakarta.annotation.Resource
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthorizeController(
    @Resource
    val service: AccountService
) {

    @GetMapping("/ask-code")
    fun askVerifyCode(
        @RequestParam email: String,
        @RequestParam type: String,
        request: HttpServletRequest
    ): RestBean<out String?> {
        val wrongMessage = service.registerEmailVerifyCode(type, email, request.remoteAddr)
        return if (wrongMessage.isBlank()) RestBean.success() else
            RestBean.failure(400, null, wrongMessage)
    }
}