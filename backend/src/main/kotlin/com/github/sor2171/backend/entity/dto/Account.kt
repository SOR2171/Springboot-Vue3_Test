package com.github.sor2171.backend.entity.dto

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.github.sor2171.backend.entity.DataCopy
import lombok.AllArgsConstructor
import java.util.*

@TableName("account")
@AllArgsConstructor
data class Account(
    @TableId(type = IdType.AUTO)
    val id: Int?,
    val username: String,
    val password: String,
    val email: String,
    val role: String,
    val registerTime: Date
) : DataCopy