/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

/**
 * Spring configuration class
 *
 * Currently configured to setup Security configuration only
 */
@Configuration @EnableWebSecurity
public class SpringConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired private DataSource dataSource;

    /**
     * Configures the AuthenticationManagerBuilder with the various
     * database details required to allow for login using the existing
     * DB used by the application.
     *
     * Passwords are encrypted via BCrypt.
     *
     * @param auth AuthenticationManagerBuilder object to be configured
     * @throws Exception if cannot be configured
     */
    @Override protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("select username, password, enabled from users where username = ?")
                .authoritiesByUsernameQuery("select username, role from user_roles where username = ?")
                .passwordEncoder(new BCryptPasswordEncoder());
    }

    /**
     * Configures which pages are secured and the View pages used by web security
     *
     * @param http HttpSecurity object to be configured
     * @throws Exception if cannot be configured
     */
    @Override protected void configure(HttpSecurity http) throws Exception {
        http.logout().logoutSuccessUrl("/AdminLogin?logout")
                .and().formLogin().loginPage("/AdminLogin")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .failureForwardUrl("/AdminLogin?error")
                        .successForwardUrl("/Admin?success")
                        .loginProcessingUrl("/DoLogin")
                .and().authorizeRequests().antMatchers("/AdminLogin").permitAll()
                .and().authorizeRequests().antMatchers("/Admin**").access("hasRole('ADMIN')")
                .and().authorizeRequests().antMatchers("/Admin**/**").access("hasRole('ADMIN')")
        .and().headers().httpStrictTransportSecurity()
                .includeSubDomains(false);
    }
}
