package com.mshift.acf.auth.jwt;

import com.google.common.net.HttpHeaders;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
@ConfigurationProperties(prefix = "application.jwt")
public class JwtConfig {

    private String secretKey;
    private String refreshKey;
    private String tokenPrefix;
    private Long tokenExpirationAfterMinutes;
    private Long refreshTokenExpirationAfterMinutes;
    private Long longTermRefreshTokenExpirationAfterDays;

    public JwtConfig() {
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getRefreshKey() {
        return refreshKey;
    }

    public void setRefreshKey(String refreshKey) {
        this.refreshKey = refreshKey;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }

    public Long getTokenExpirationAfterMinutes() {
        return tokenExpirationAfterMinutes;
    }

    public void setTokenExpirationAfterMinutes(Long tokenExpirationAfterMinutes) {
        this.tokenExpirationAfterMinutes = tokenExpirationAfterMinutes;
    }

    public Long getRefreshTokenExpirationAfterMinutes() {
        return refreshTokenExpirationAfterMinutes;
    }

    public void setRefreshTokenExpirationAfterMinutes(Long refreshTokenExpirationAfterMinutes) {
        this.refreshTokenExpirationAfterMinutes = refreshTokenExpirationAfterMinutes;
    }

    public Long getLongTermRefreshTokenExpirationAfterDays() {
        return longTermRefreshTokenExpirationAfterDays;
    }

    public void setLongTermRefreshTokenExpirationAfterDays(Long longTermRefreshTokenExpirationAfterDays) {
        this.longTermRefreshTokenExpirationAfterDays = longTermRefreshTokenExpirationAfterDays;
    }

    public String getAuthorizationHeader() {
        return HttpHeaders.AUTHORIZATION;
    }

    public String getRefreshTokenHeader() {
        return "RefreshToken";
    }

    public LocalDateTime getAccessTokenExpiration() {
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(getTokenExpirationAfterMinutes());
        return expiration;
    }

    public LocalDateTime getRefreshTokenExpiration() {
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(getRefreshTokenExpirationAfterMinutes());
        return expiration;
    }

    public LocalDateTime getLongTermRefreshTokenExpiration() {
        LocalDateTime expiration = LocalDateTime.now().plusDays(getLongTermRefreshTokenExpirationAfterDays());
        return expiration;
    }
}
