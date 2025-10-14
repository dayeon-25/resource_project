package com.example.resource.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final LoginFailureHandler loginFailureHandler;

    public SecurityConfig(LoginFailureHandler loginFailureHandler) {
        this.loginFailureHandler = loginFailureHandler;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.formLogin((formLogin)->formLogin
                        .loginPage("/login")
                        .failureHandler(loginFailureHandler)
                        .permitAll()

                )
                .logout((logout)->logout.logoutUrl("/logout"))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/error","/css/**", "webjars/**","images/**").permitAll()
                        .requestMatchers("/", "/about", "/analyze").permitAll()
                        .requestMatchers("/api/pred").permitAll()
                        .requestMatchers("/signup").permitAll()
                        .anyRequest().authenticated());
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}