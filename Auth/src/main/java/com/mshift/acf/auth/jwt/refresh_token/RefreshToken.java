package com.mshift.acf.auth.jwt.refresh_token;

import com.mshift.acf.auth.utils.Utility;
import io.jsonwebtoken.Jwts;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "refreshTokens")
public class RefreshToken {
    @Id
    private String id;
    private String jti;
    private String subject;
    private LocalDateTime issuedAt;
    private LocalDateTime expiration;
    @Transient
    private String tokenValue;
    @Transient
    private SecretKey secretKey;

    public RefreshToken() {
    }

    public RefreshToken(String subject, LocalDateTime issuedAt, LocalDateTime expiration, SecretKey secretKey) {
        this.subject = subject;
        this.issuedAt = issuedAt;
        this.expiration = expiration;
        this.secretKey = secretKey;

        jti = UUID.randomUUID().toString();
        tokenValue = Jwts.builder()
                    .setSubject(subject)
                    .claim("jti", jti)
                    .setIssuedAt(Utility.convertLdtToDate(issuedAt))
                    .setExpiration(Utility.convertLdtToDate(expiration))
                    .signWith(this.secretKey)
                    .compact();

    }

    public String getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }

    public String getTokenValue() {
        return tokenValue;
    }
}
