package com.pokedex.bff.infrastructure.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    @Profile("dev")
    fun devFilterChain(http: HttpSecurity): SecurityFilterChain {
        // CSRF desabilitado porque este BFF é uma API stateless (sem sessão/cookies).
        // Para endpoints com autenticação baseada em token, o risco de CSRF não se aplica.
        http.csrf { it.disable() }
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
        return http.build()
    }

    @Bean
    @Profile("prod")
    fun prodFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/actuator/health", "/actuator/info").permitAll()
                it.anyRequest().authenticated()
            }
            .httpBasic { }
            .formLogin { it.disable() }
        return http.build()
    }

    @Bean
    @Profile("prod")
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    @Profile("prod")
    fun userDetailsService(
        @Value("\${app.security.basic.username}") username: String,
        @Value("\${app.security.basic.password}") password: String,
        passwordEncoder: PasswordEncoder
    ): UserDetailsService {
        val user = User.withUsername(username)
            .password(passwordEncoder.encode(password))
            .roles("USER")
            .build()
        return InMemoryUserDetailsManager(user)
    }
}
