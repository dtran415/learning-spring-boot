package com.mshift.acf.auth.jwt;

import com.mshift.acf.auth.jwt.refresh_token.RefreshToken;
import com.mshift.acf.auth.jwt.refresh_token.RefreshTokenService;
import com.mshift.acf.auth.utils.Utility;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;

@Component
public class JwtHelper {
    private JwtConfig jwtConfig;
    private JwtSecretKey jwtSecretKey;

    @Autowired
    public JwtHelper(JwtConfig jwtConfig, JwtSecretKey jwtSecretKey) {
        this.jwtConfig = jwtConfig;
        this.jwtSecretKey = jwtSecretKey;
    }

    public String generateAccessToken(String subject, Collection<? extends GrantedAuthority> authorities) {
        LocalDateTime expiration = jwtConfig.getAccessTokenExpiration();
        SecretKey secretKey = jwtSecretKey.secretKey();
        String token = Jwts.builder()
                .setSubject(subject)
                .claim("authorities", authorities)
                .setIssuedAt(new Date())
                .setExpiration(Utility.convertLdtToDate(expiration))
                .signWith(secretKey)
                .compact();

        return token;
    }

    public String getAuthorizationHeader() {
        return jwtConfig.getAuthorizationHeader();
    }

    public String getTokenPrefix() {
        return jwtConfig.getTokenPrefix();
    }

    public LocalDateTime getRefreshTokenExpiration(){
        return jwtConfig.getRefreshTokenExpiration();
    }

    public LocalDateTime getLongTermRefreshTokenExpiration() {
        return jwtConfig.getLongTermRefreshTokenExpiration();
    }

    public LocalDateTime getAccessTokenExpiration() {
        return jwtConfig.getAccessTokenExpiration();
    }

    public SecretKey getRefreshKey() {
        return jwtSecretKey.refreshKey();
    }

    public SecretKey getSecretKey() {
        return jwtSecretKey.secretKey();
    }

    public String getRefreshTokenHeader() {
        return jwtConfig.getRefreshTokenHeader();
    }
    
    
    public void generateTokens(HttpServletResponse response, RefreshTokenService refreshTokenService,
                                      String name, Collection<? extends GrantedAuthority> authorities, Boolean stayLoggedIn) {

        String token = generateAccessToken(name, authorities);

        response.addHeader(getAuthorizationHeader(), getTokenPrefix() + token);

        LocalDateTime expirationDate = getRefreshTokenExpiration();

        if (stayLoggedIn) {
            expirationDate = getLongTermRefreshTokenExpiration();
        }

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(name, expirationDate);

        response.addHeader(getRefreshTokenHeader(), getTokenPrefix() + refreshToken.getTokenValue());

    }
}

