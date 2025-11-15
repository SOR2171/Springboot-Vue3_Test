package com.github.sor2171.backend.controller

import com.github.sor2171.backend.entity.RestBean
import com.github.sor2171.backend.entity.vo.request.EmailRegisterVO
import com.github.sor2171.backend.service.AccountService
import jakarta.annotation.Resource
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/api/auth")
class AuthorizeController(
    @Resource
    val service: AccountService
) {

    @GetMapping("/ask-code")
    fun askVerifyCode(
        @RequestParam @NotBlank @Email email: String,
        @RequestParam @Pattern(regexp = "(register|reset)") type: String,
        request: HttpServletRequest
    ): RestBean<out String?> {
        return this.messageHandler(
            service
                .registerEmailVerifyCode(
                    type,
                    email,
                    request.remoteAddr
                )
        )
    }

    @PostMapping("/register")
    fun emailRegister(@RequestBody @Valid vo: EmailRegisterVO): RestBean<out String?> {
        return this.messageHandler(service.registerEmailAccount(vo))
    }

    fun messageHandler(wrongMessage: String): RestBean<out String?> {
        return if (wrongMessage.isBlank()) {
            RestBean.success()
        } else {
            RestBean.failure(400, null, wrongMessage)
        }
    }
}