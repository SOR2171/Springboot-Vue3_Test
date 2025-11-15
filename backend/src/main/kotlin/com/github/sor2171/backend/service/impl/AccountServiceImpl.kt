package com.github.sor2171.backend.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.github.sor2171.backend.entity.dto.Account
import com.github.sor2171.backend.mapper.AccountMapper
import com.github.sor2171.backend.service.AccountService
import com.github.sor2171.backend.utils.Const
import com.github.sor2171.backend.utils.FlowUtils
import jakarta.annotation.Resource
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class AccountServiceImpl(
    @Resource
    val utils: FlowUtils,

    @Resource
    val amqpTemplate: AmqpTemplate,

    @Resource
    val stringRedisTemplate: StringRedisTemplate

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

    override fun registerEmailVerifyCode(type: String, email: String, ip: String): String {
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

    fun verifyLimit(ip: String): Boolean {
        val key = Const.VERIFY_EMAIL_LIMIT + ip
        return utils.limitOnceCheck(key, 60)
    }
}