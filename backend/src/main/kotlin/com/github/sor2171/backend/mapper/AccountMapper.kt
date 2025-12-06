package com.github.sor2171.backend.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.github.sor2171.backend.entity.dto.Account
import org.apache.ibatis.annotations.Mapper

@Mapper
interface AccountMapper : BaseMapper<Account>