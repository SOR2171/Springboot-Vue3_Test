package com.github.sor2171.backend.service

import com.baomidou.mybatisplus.extension.service.IService
import com.github.sor2171.backend.entity.dto.Account
import com.github.sor2171.backend.entity.vo.request.EmailRegisterVO
import com.github.sor2171.backend.entity.vo.request.PasswordResetVO
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import reactor.core.publisher.Mono

interface AccountService : IService<Account>, ReactiveUserDetailsService {
    fun findAccountByNameOrEmail(text: String): Mono<Account>
    fun askEmailVerifyCode(type: String, email: String, ip: String): Mono<String>
    fun registerEmailAccount(vo: EmailRegisterVO): Mono<String>
    fun resetEmailAccountPassword(vo: PasswordResetVO): Mono<String>
    fun invalidateJwt(headerToken: String?): Boolean
}