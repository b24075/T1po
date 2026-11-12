package oit.is.t1.t1project.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login", "/error").permitAll()
            .anyRequest().authenticated())
        .formLogin(login -> login
            .loginPage("/login")
            .defaultSuccessUrl("/mode", true)
            .failureUrl("/login?error=true")
            .permitAll())
        .logout(logout -> logout
            .logoutSuccessUrl("/login?logout=true")
            .permitAll());
    return http.build();
  }

  @Bean
  InMemoryUserDetailsManager userDetailsService() {
    return new InMemoryUserDetailsManager(
        User.withUsername("player1")
            .password("{noop}password")
            .roles("PLAYER")
            .build(),
        User.withUsername("player2")
            .password("{noop}password")
            .roles("PLAYER")
            .build());
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/login").setViewName("login");
    registry.addViewController("/mode").setViewName("mode");
  }
}
