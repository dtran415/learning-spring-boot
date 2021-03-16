package com.mshift.acf.user_services.jwt;

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
