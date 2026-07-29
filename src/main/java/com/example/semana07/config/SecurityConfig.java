package com.example.semana07.config;

import com.example.semana07.security.LoginSuccessHandler;
import com.example.semana07.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired private CustomUserDetailsService userDetailsService;
    @Autowired private LoginSuccessHandler loginSuccessHandler;
    @Autowired private PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Públicas
                        .requestMatchers("/", "/login", "/registro", "/register", "/error", "/test").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/vendors/**").permitAll()
                        .requestMatchers("/fanarts", "/arte", "/descartados", "/gallery/**", "/content/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                        // Lectura pública de contenido (galería)
                        .requestMatchers(HttpMethod.GET, "/api/arte/**", "/api/videos/**", "/api/personajes/**",
                                "/api/slider/**", "/api/logo/**", "/api/comentarios/**", "/api/likes/**").permitAll()

                        // Escritura de contenido: solo ADMIN / SUBADMIN
                        .requestMatchers(HttpMethod.POST, "/api/arte/**", "/api/videos/**", "/api/personajes/**",
                                "/api/logo/**", "/api/slider/**").hasAnyRole("ADMIN", "SUBADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/arte/**", "/api/videos/**", "/api/personajes/**",
                                "/api/logo/**", "/api/slider/**").hasAnyRole("ADMIN", "SUBADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/arte/**", "/api/videos/**", "/api/personajes/**",
                                "/api/logo/**", "/api/slider/**").hasAnyRole("ADMIN", "SUBADMIN")

                        // Interacción de cualquier usuario autenticado
                        .requestMatchers(HttpMethod.POST, "/api/comentarios/**", "/api/likes/**", "/api/reportes/**")
                        .authenticated()
                        .requestMatchers("/api/notificaciones/**").authenticated()

                        // Administración
                        .requestMatchers("/dashboard/admin/**", "/admin/**", "/api/admin/**", "/api/usuarios/**",
                                "/api/historial/**", "/api/exportar/**", "/api/reportes/admin/**").hasRole("ADMIN")
                        .requestMatchers("/dashboard/subadmin/**").hasAnyRole("ADMIN", "SUBADMIN")
                        .requestMatchers("/dashboard/**", "/user/**").authenticated()

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler(loginSuccessHandler)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .permitAll()
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/api/**"))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return authBuilder.build();
    }
}