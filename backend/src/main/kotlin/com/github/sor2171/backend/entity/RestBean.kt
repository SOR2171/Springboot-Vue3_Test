package com.github.sor2171.backend.entity

import com.alibaba.fastjson2.JSONObject
import com.alibaba.fastjson2.JSONWriter

data class RestBean<T>(
    val code: Int,
    val data: T,
    val message: String
) {
    companion object {
        fun success(data: Any? = null, message: String = "Success"): RestBean<Any?> {
            return RestBean(200, data, message)
        }

        fun <T> failure(code: Int = 401, data: T, message: String?): RestBean<T> {
            return RestBean(code, data, message ?: "Failure")
        }

        fun unauthenticated(message: String?) =
            failure(401, null, message)

        fun forbidden(message: String?) =
            failure(403, null, message)

        fun logoutFailed(message: String = ""): RestBean<Any?> {
            val formatMessage = if (message.isBlank()) "" else ": $message"
            return failure(400, null, "Logout Failed$formatMessage")
        }
    }

    fun toJsonString(): String =
        JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls)
}