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
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.burgas.companyspringboot.entity.identity.Authority.DIRECTOR;
import static org.burgas.companyspringboot.entity.identity.Authority.EMPLOYEE;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(this.customUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder);
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.csrfTokenRequestHandler(new XorCsrfTokenRequestAttributeHandler()))
                .cors(cors -> cors.configurationSource(new UrlBasedCorsConfigurationSource()))
                .httpBasic(httpBasic -> httpBasic
                        .securityContextRepository(new RequestAttributeSecurityContextRepository()))
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
                                        "/api/v1/identities/update", "/api/v1/identities/delete"
                                )
                                .hasAnyAuthority(DIRECTOR.getAuthority(), EMPLOYEE.getAuthority())
                )
                .build();
    }
}
