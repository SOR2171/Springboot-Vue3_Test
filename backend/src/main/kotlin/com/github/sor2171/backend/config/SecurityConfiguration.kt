package com.github.sor2171.backend.config

import com.github.sor2171.backend.entity.RestBean
import com.github.sor2171.backend.entity.vo.response.AuthorizeVO
import com.github.sor2171.backend.filter.JwtAuthorizeFilter
import com.github.sor2171.backend.utils.JwtUtils
import jakarta.annotation.Resource
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfiguration(
    @Resource
    val utils: JwtUtils,

    @Resource
    val jwtAuthorizeFilter: JwtAuthorizeFilter
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests {
                it
                    .requestMatchers("/api/auth/**").permitAll()
                    .anyRequest().authenticated()
            }
            .formLogin {
                it
                    .loginProcessingUrl("/api/auth/login")
                    .failureHandler(authenticationFailureHandler)
                    .successHandler(authenticationSuccessHandler)
            }
            .logout {
                it
                    .logoutUrl("/api/auth/logout")
                    .logoutSuccessHandler(logoutSuccessHandler)
            }
            .exceptionHandling {
                it
                    .authenticationEntryPoint(unauthenticatedHandler)
                    .accessDeniedHandler(accessDeniedHandler)
            }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

    val authenticationSuccessHandler =
        { request: HttpServletRequest,
          response: HttpServletResponse,
          authentication: Authentication ->
            response.contentType = "application/json;charset=UTF-8"

            val user = authentication.principal as UserDetails
            val vo = AuthorizeVO(
                "abc123",
                "USER",
                utils.createJwt(
                    user,
                    1,
                    "abc123"
                ),
                utils.expiresTime()
            )
            
            response.writer.write(
                RestBean
                    .success(vo)
                    .toJsonString()
            )
        }

    val authenticationFailureHandler =
        { request: HttpServletRequest,
          response: HttpServletResponse,
          exception: Exception ->
            response.contentType = "application/json;charset=UTF-8"
            response.writer.write(
                RestBean
                    .unauthenticated(exception.message)
                    .toJsonString()
            )
        }

    val logoutSuccessHandler =
        { request: HttpServletRequest,
          response: HttpServletResponse,
          authentication: Authentication? ->
            response.contentType = "application/json;charset=UTF-8"
            val writer = response.writer
            val authorization = request.getHeader(HttpHeaders.AUTHORIZATION)
            if (utils.invalidateJwt(authorization)) 
                writer.write(RestBean.success().toJsonString())
            else writer.write(RestBean.logoutFailed().toJsonString())
        }

    val unauthenticatedHandler =
        { request: HttpServletRequest,
          response: HttpServletResponse,
          authException: AuthenticationException ->
            response.contentType = "application/json;charset=UTF-8"
            response.writer.write(
                RestBean
                    .unauthenticated(authException.message)
                    .toJsonString()
            )
        }

    val accessDeniedHandler =
        { request: HttpServletRequest,
          response: HttpServletResponse,
          accessDeniedException: AccessDeniedException ->
            response.contentType = "application/json;charset=UTF-8"
            response.writer.write(
                RestBean
                    .forbidden(accessDeniedException.message)
                    .toJsonString()
            )
        }
}