package com.burbujas.gestionlimpia.config;

import com.burbujas.gestionlimpia.models.repositories.IConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final IConfigRepository configRepository;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public WebSecurityConfig(IConfigRepository configRepository, BCryptPasswordEncoder encoder) {
        this.configRepository = configRepository;
        this.encoder = encoder;

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/vendor/**", "/js/**", "/webjars/**", "/administracion/clave/cambiar/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .invalidSessionUrl("/login")
                        .maximumSessions(1)
                        .expiredUrl("/login")
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .passwordParameter("password")
                        .usernameParameter("email")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID") // borra la cookie de la sesión
                        .clearAuthentication(true)
                        .permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable)  // Solo si es necesario
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(this.encoder);
        return provider;
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        String emailAcceso = this.configRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Configuración de administración no encontrada."))
                .getEmailAcceso();

        String claveAcceso = this.configRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Configuración de administración no encontrada."))
                .getClaveAcceso();

        UserDetails user = User.builder()
                .username(emailAcceso)
                .password(claveAcceso)
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
}