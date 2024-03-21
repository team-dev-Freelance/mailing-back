package com.api.mailing.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static com.api.mailing.entities.Permission.ADMIN_CREATE;
import static com.api.mailing.entities.Permission.ADMIN_DELETE;
import static com.api.mailing.entities.Permission.ADMIN_READ;
import static com.api.mailing.entities.Permission.ADMIN_UPDATE;
import static com.api.mailing.entities.Permission.USER_CREATE;
import static com.api.mailing.entities.Permission.USER_DELETE;
import static com.api.mailing.entities.Permission.USER_READ;
import static com.api.mailing.entities.Permission.USER_UPDATE;
import static com.api.mailing.entities.Role.ADMIN;
import static com.api.mailing.entities.Role.USER;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;
        private final LogoutHandler logoutHandler;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.csrf().disable()
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/api/v1/auth/**",
                                                                "/v2/api-docs",
                                                                "/v3/api-docs",
                                                                "/v3/api-docs/**",
                                                                "/swagger-resources",
                                                                "/swagger-resources/**",
                                                                "/configuration/ui",
                                                                "/configuration/security",
                                                                "/swagger-ui/**",
                                                                "/webjars/**",
                                                                "/swagger-ui.html")
                                                .permitAll()
                                                .requestMatchers("/api/v1/admin/**").hasAnyRole(ADMIN.name())
                                                .requestMatchers("/api/v1/user/**")
                                                .hasAnyRole(ADMIN.name(), USER.name())
                                                .requestMatchers(HttpMethod.GET, "/api/v1/user/**")
                                                .hasAnyAuthority(ADMIN_READ.name(), USER_READ.name())
                                                .requestMatchers(HttpMethod.POST, "/api/v1/user/**")
                                                .hasAnyAuthority(ADMIN_CREATE.name(), USER_CREATE.name())
                                                .requestMatchers(HttpMethod.PUT, "/api/v1/user/**")
                                                .hasAnyAuthority(ADMIN_UPDATE.name(), USER_UPDATE.name())
                                                .requestMatchers(HttpMethod.DELETE, "/api/v1/user/**")
                                                .hasAnyAuthority(ADMIN_DELETE.name(), USER_DELETE.name())
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                                .logout(logout -> logout
                                                .logoutUrl("/api/v1/auth/logout")
                                                .addLogoutHandler(logoutHandler)
                                                .logoutSuccessHandler(
                                                                (request, response,
                                                                                authentication) -> SecurityContextHolder
                                                                                                .clearContext()));

                return http.build();
        }
}
