package com.github.sor2171.backend.service.impl

import com.baomidou.mybatisplus.core.toolkit.Wrappers
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.github.sor2171.backend.entity.dto.Account
import com.github.sor2171.backend.entity.vo.request.EmailRegisterVO
import com.github.sor2171.backend.entity.vo.request.PasswordResetVO
import com.github.sor2171.backend.mapper.AccountMapper
import com.github.sor2171.backend.service.AccountService
import com.github.sor2171.backend.utils.Const
import com.github.sor2171.backend.utils.FlowUtils
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class AccountServiceImpl(
    private val utils: FlowUtils,
    private val amqpTemplate: AmqpTemplate,
    private val stringRedisTemplate: StringRedisTemplate,
    private val encoder: PasswordEncoder

) : ServiceImpl<AccountMapper, Account>(), AccountService {

    override fun loadUserByUsername(username: String?): UserDetails? {
        val account = username?.let { findAccountByNameOrEmail(it) }
        if (account == null)
            throw UsernameNotFoundException("Account with name $username not found")
        return User
            .withUsername(username)
            .password(account.password)
            .roles(account.role)
            .build()
    }

    override fun findAccountByNameOrEmail(text: String): Account? {
        return this.query()
            .eq("username", text).or()
            .eq("email", text)
            .one()
    }

    override fun askEmailVerifyCode(type: String, email: String, ip: String): String {
        synchronized(ip.intern()) {
            if (this.verifyLimit(ip)) {
                val code = (100000..999999).random().toString()
                val data = mapOf(
                    "type" to type,
                    "email" to email,
                    "code" to code
                )
                amqpTemplate.convertAndSend("mail", data)
                stringRedisTemplate
                    .opsForValue()
                    .set(
                        Const.VERIFY_EMAIL_DATA + email,
                        code,
                        3,
                        TimeUnit.MINUTES
                    )
                return ""
            } else {
                return "Request limit exceeded. Please try again later."
            }
        }
    }

    override fun registerEmailAccount(vo: EmailRegisterVO): String {
        val (email, code, username, password) = vo

        verifyCode(email, code).also {
            if (it != null) return it
        }

        if (this.existAccountByEmail(email)) return "account with the same email already exists."
        if (this.existAccountByUsername(username)) return "username already exists."

        val encodedPassword = encoder.encode(password)
        val account = Account(
            null,
            username,
            encodedPassword,
            email,
            "user",
            Date()
        )

        if (this.save(account)) {
            stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + email)
            return ""
        } else {
            return "something went wrong. Please contact the administrator."
        }
    }

    override fun resetEmailAccountPassword(vo: PasswordResetVO): String {
        val (email, code, password) = vo

        verifyCode(email, code).also {
            if (it != null) return it
        }

        if (!existAccountByEmail(email))
            return "account with the email does not exist."

        val encodedPassword = encoder.encode(password)

        val update = this.update()
            .eq("email", email)
            .set("password", encodedPassword)
            .update()

        if (update) {
            stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + email)
            return ""
        } else {
            return "something went wrong. Please contact the administrator."
        }
    }

    private fun existAccountByEmail(email: String): Boolean {
        return this.baseMapper.exists(
            Wrappers
                .query<Account>()
                .eq("email", email)
        )
    }

    private fun existAccountByUsername(username: String): Boolean {
        return this.baseMapper.exists(
            Wrappers
                .query<Account>()
                .eq("username", username)
        )
    }

    private fun verifyLimit(ip: String): Boolean {
        val key = Const.VERIFY_EMAIL_LIMIT + ip
        return utils.limitOnceCheck(key, 60)
    }

    private fun verifyCode(email: String, receivedCode: String?): String? {
        val code = stringRedisTemplate
            .opsForValue()
            .get(Const.VERIFY_EMAIL_DATA + email)

        if (code == null) return "verify code has not been sent"
        if (receivedCode == null || code != receivedCode) return "verify code is wrong."
        return null
    }
}