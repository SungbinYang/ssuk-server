package com.ssuk.global.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${app.host.client}")
    private String corsOrigin;

    private final Environment environment;

    private static final List<String> WHITE_LIST = List.of("/api/auth/signup", "/api/auth/login");

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
                .httpBasic(HttpBasicConfigurer::disable)
                .formLogin(FormLoginConfigurer::disable)
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> {
                    // 허용 url
                    WHITE_LIST.forEach(url -> authorizationManagerRequestMatcherRegistry.requestMatchers(url).permitAll());

                    // actuator와 rest docs 부분 허용
                    authorizationManagerRequestMatcherRegistry.requestMatchers("/actuator/**", "/docs/**").hasRole("ADMIN");

                    authorizationManagerRequestMatcherRegistry.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();

                    // 테스트 profile일 경우 /h2-console 허용
                    if (Arrays.asList(this.environment.getActiveProfiles()).contains("test")) {
                        authorizationManagerRequestMatcherRegistry.requestMatchers(PathRequest.toH2Console()).permitAll();
                    }

                    authorizationManagerRequestMatcherRegistry.anyRequest().authenticated();
                });

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern(corsOrigin);
        configuration.setAllowedMethods(Arrays.asList(GET.name(), POST.name(), PUT.name(), PATCH.name(), DELETE.name()));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
