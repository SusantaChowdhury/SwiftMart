package com.susanta.SwiftMart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.susanta.SwiftMart.services.impl.SecurityCustomeUserDetailService;

@Configuration
public class SecurityConfig {
    // create user and login using java code with in memory service
    // @Bean
    // public UserDetailsService userDetailsService() {
    // UserDetails user1 = User.withUsername("admin123")
    // .password("admin123")
    // .roles("ADMIN", "USER")
    // .build();

    // UserDetails user2 = User.withUsername("user123")
    // .password("user123")
    // // .roles()
    // .build();

    // return new InMemoryUserDetailsManager(user1, user2);
    // }

    @Autowired
    private SecurityCustomeUserDetailService userDetailsService;
    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    // configuration of authentication provider
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);// using the userDetailsService method to
                                                                            // pass the object of the
                                                                            // SecurityCustomeUserDetailService class
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());// using the passwordEncoder method to pass the
                                                                        // object of the BCryptPasswordEncoder class
        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        // confifuration
        httpSecurity.authorizeHttpRequests(authorize -> {
            // authorize.requestMatchers("/home", "/signup", "/login",
            // "../image/shopping.png").permitAll();
            authorize.requestMatchers("/seller/**").hasRole("SELLER");
            authorize.requestMatchers("/customer/**").hasRole("CUSTOMER");
            authorize.requestMatchers("/admin/**").hasRole("ADMIN");
            authorize.anyRequest().permitAll();
        });

        // form login related configuration
        httpSecurity.formLogin(formLogin -> {
            formLogin.loginPage("/login")
                    .loginProcessingUrl("/authenticate")
                    .failureUrl("/login?error=true")
                    // .defaultSuccessUrl("/user/dashboard", true)
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .successHandler(customAuthenticationSuccessHandler);
        }); // this method is used to create a login form for the user

        // disable the CSRF protection for the application
        httpSecurity.csrf(csrf -> csrf.disable()); // this method is used to disable the CSRF protection for the
                                                   // application
        httpSecurity.logout(logoutForm -> {
            logoutForm.logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout=true")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID");
        }); // this method is used to create a logout form for the use

        // Session management configuration
        httpSecurity.sessionManagement(sessionManagement -> {
            sessionManagement.maximumSessions(1) // Allow only one session per user
                    .maxSessionsPreventsLogin(false) // Allow new login to invalidate the old session
                    .sessionRegistry(sessionRegistry());
        });

        return httpSecurity.build(); // Return the configured SecurityFilterChain
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity
                .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider());
        return authenticationManagerBuilder.build();
    }

    // the method creates a bean of the PasswordEncoder type and returns an object
    // of the BCryptPasswordEncoder class
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Session registry bean to manage active sessions
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
    
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
