package org.burgas.companyspringboot.config;

import lombok.RequiredArgsConstructor;
import org.burgas.companyspringboot.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.burgas.companyspringboot.entity.identity.Authority.DIRECTOR;
import static org.burgas.companyspringboot.entity.identity.Authority.EMPLOYEE;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public XorCsrfTokenRequestAttributeHandler xorCsrfTokenRequestAttributeHandler() {
        return new XorCsrfTokenRequestAttributeHandler();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return urlBasedCorsConfigurationSource;
    }

    @Bean
    public HttpSessionSecurityContextRepository httpSessionSecurityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(this.customUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder);
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.csrfTokenRequestHandler(this.xorCsrfTokenRequestAttributeHandler()))
                .cors(cors -> cors.configurationSource(this.corsConfigurationSource()))
                .httpBasic(httpBasic -> httpBasic.securityContextRepository(
                        this.httpSessionSecurityContextRepository())
                )
                .authenticationManager(this.authenticationManager())
                .authorizeHttpRequests(
                        requests -> requests

                                .requestMatchers(
                                        "/api/v1/security/csrf-token",

                                        "/api/v1/companies", "/api/v1/companies/by-id",

                                        "/api/v1/identities/create"
                                )
                                .permitAll()

                                .requestMatchers(
                                        "/api/v1/companies/create-update", "/api/v1/companies/delete",

                                        "/api/v1/identities", "/api/v1/identities/by-id", "/api/v1/identities/add-company",
                                        "/api/v1/identities/update", "/api/v1/identities/delete",
                                        "/api/v1/identities/change-password", "/api/v1/identities/add-chat",

                                        "/api/v1/chats/by-identity", "/api/v1/chats/by-id",
                                        "/api/v1/chats/create-update", "/api/v1/chats/delete",

                                        "/api/v1/messages/by-chat", "/api/v1/messages/by-id",
                                        "/api/v1/messages/create-update", "/api/v1/messages/delete",

                                        "/api/v1/wallets/by-identity", "/api/v1/wallets/by-id",
                                        "/api/v1/wallets/create-update", "/api/v1/wallets/delete",
                                        "/api/v1/wallets/deposit", "/api/v1/wallets/withdraw", "/api/v1/wallets/transfer",

                                        "/api/v1/operations/by-id", "/api/v1/operations/delete"
                                )
                                .hasAnyAuthority(DIRECTOR.getAuthority(), EMPLOYEE.getAuthority())
                )
                .build();
    }
}
