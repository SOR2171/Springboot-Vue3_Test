package com.github.sor2171.backend.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.github.sor2171.backend.entity.dto.Account
import com.github.sor2171.backend.mapper.AccountMapper
import com.github.sor2171.backend.service.AccountService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AccountServiceImpl : ServiceImpl<AccountMapper, Account>(), AccountService {
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
}