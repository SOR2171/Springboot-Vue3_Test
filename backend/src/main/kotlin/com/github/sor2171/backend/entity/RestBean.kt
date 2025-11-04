package com.github.sor2171.backend.entity

import com.alibaba.fastjson2.JSONObject
import com.alibaba.fastjson2.JSONWriter

data class RestBean<T>(
    val code: Int,
    val data: T,
    val message: String
) {
    companion object {
        fun <T> success(data: T, message: String = "Success"): RestBean<T> {
            return RestBean(200, data, message)
        }

        fun success(): RestBean<String?> = success(null)

        fun <T> failure(code: Int = 401, data: T, message: String?): RestBean<T> {
            return RestBean(code, data, message ?: "Failure")
        }

        fun unauthenticated(message: String?) = failure(401, null, message)
        
        fun forbidden(message: String?) = failure(403, null, message)
    }

    fun toJsonString(): String = JSONObject.toJSONString(
        this,
        JSONWriter.Feature.WriteNulls
    )
}