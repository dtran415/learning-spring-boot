package com.mshift.acf.auth.security;

import com.mshift.acf.auth.application_user.ApplicationUserService;
import com.mshift.acf.auth.jwt.JwtHelper;
import com.mshift.acf.auth.jwt.JwtTokenVerifier;
import com.mshift.acf.auth.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import com.mshift.acf.auth.jwt.refresh_token.RefreshTokenDao;
import com.mshift.acf.auth.jwt.refresh_token.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final ApplicationUserService applicationUserService;
    private final PasswordEncoder passwordEncoder;
    private final JwtHelper jwtHelper;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenDao refreshTokenDao;

    @Autowired
    public ApplicationSecurityConfig(ApplicationUserService applicationUserService, PasswordEncoder passwordEncoder, JwtHelper jwtHelper, RefreshTokenService refreshTokenService, RefreshTokenDao refreshTokenDao) {
        this.applicationUserService = applicationUserService;
        this.passwordEncoder = passwordEncoder;
        this.jwtHelper = jwtHelper;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenDao = refreshTokenDao;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtHelper, refreshTokenService))
                .addFilterAfter(new JwtTokenVerifier(jwtHelper), JwtUsernameAndPasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/register", "/token").permitAll()
                .anyRequest().authenticated()
                .and()
                .logout()
                    .addLogoutHandler(new CustomLogoutHandler(jwtHelper, refreshTokenDao))
                    .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                    .permitAll();
    }

    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(applicationUserService);

        return  provider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }
}
