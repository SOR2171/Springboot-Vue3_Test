package com.github.sor2171.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/test")
class TestController {
    
    @GetMapping("/hello")
    fun test() = "Hello Kotlin Springboot!"
}