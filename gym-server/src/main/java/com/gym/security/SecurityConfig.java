package com.gym.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            // 登录、注册接口允许匿名访问
            .antMatchers("/api/admin/login", "/api/front/login", "/api/front/register").permitAll()
            // 上传的静态资源允许公开访问
            .antMatchers("/upload/**").permitAll()
            // 前台 GET 请求（浏览）允许匿名
            .antMatchers(HttpMethod.GET, "/api/front/**").permitAll()
            // 管理端接口需要认证
            .antMatchers("/api/admin/**").authenticated()
            // 前台 POST/PUT/DELETE（会员操作）需要认证
            .antMatchers(HttpMethod.POST, "/api/front/**").authenticated()
            .antMatchers(HttpMethod.PUT, "/api/front/**").authenticated()
            .antMatchers(HttpMethod.DELETE, "/api/front/**").authenticated()
            // 其他请求允许访问
            .anyRequest().permitAll()
            .and()
            // 未认证异常处理
            .exceptionHandling()
            .authenticationEntryPoint((request, response, authException) -> {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                response.setStatus(401);
                ObjectMapper objectMapper = new ObjectMapper();
                response.getWriter().write(objectMapper.writeValueAsString(Result.error(401, "未登录或token已过期")));
            })
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
