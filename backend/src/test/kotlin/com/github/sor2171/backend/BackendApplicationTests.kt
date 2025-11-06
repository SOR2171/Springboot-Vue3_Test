package com.github.sor2171.backend

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootTest
class BackendApplicationTests {

    @Test
    fun contextLoads() {
        println(BCryptPasswordEncoder().encode("123456"))
    }

}
