package com.mshift.acf.api_gateway.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;

@Component
public class JwtHelper {

    private JwtConfig jwtConfig;
    private JwtSecretKey jwtSecretKey;

    @Autowired
    public JwtHelper(JwtConfig jwtConfig, JwtSecretKey jwtSecretKey) {
        this.jwtConfig = jwtConfig;
        this.jwtSecretKey = jwtSecretKey;
    }

    public String getAuthorizationHeader() {
        return jwtConfig.getAuthorizationHeader();
    }

    public String getRefreshTokenHeader() {
        return jwtConfig.getRefreshTokenHeader();
    }

    public String getTokenPrefix() {
        return jwtConfig.getTokenPrefix();
    }

    public LocalDateTime getAccessTokenExpiration() {
        return jwtConfig.getAccessTokenExpiration();
    }

    public SecretKey getSecretKey() {
        return jwtSecretKey.secretKey();
    }

}
