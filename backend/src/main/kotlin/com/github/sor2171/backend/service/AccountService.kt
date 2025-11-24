package com.github.sor2171.backend.service

import com.baomidou.mybatisplus.extension.service.IService
import com.github.sor2171.backend.entity.dto.Account
import com.github.sor2171.backend.entity.vo.request.EmailRegisterVO
import com.github.sor2171.backend.entity.vo.request.PasswordResetVO
import org.springframework.security.core.userdetails.UserDetailsService

interface AccountService : IService<Account>, UserDetailsService {
    fun findAccountByNameOrEmail(text: String): Account?
    fun askEmailVerifyCode(type: String, email: String, ip: String): String
    fun registerEmailAccount(vo: EmailRegisterVO): String
    fun resetEmailAccountPassword(vo: PasswordResetVO): String
}