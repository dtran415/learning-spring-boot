package com.mshift.acf.auth.jwt.refresh_token;

import com.mshift.acf.auth.application_user.ApplicationUser;
import com.mshift.acf.auth.application_user.ApplicationUserService;
import com.mshift.acf.auth.jwt.JwtHelper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RefreshTokenService {

    private final RefreshTokenDao refreshTokenDao;
    private final JwtHelper jwtHelper;
    private final ApplicationUserService applicationUserService;

    @Autowired
    public RefreshTokenService(RefreshTokenDao refreshTokenDao, JwtHelper jwtHelper, ApplicationUserService applicationUserService) {
        this.refreshTokenDao = refreshTokenDao;
        this.jwtHelper = jwtHelper;
        this.applicationUserService = applicationUserService;
    }

    //create refresh token
    public RefreshToken createRefreshToken(String subject, LocalDateTime expiration) {
        SecretKey refreshKey = jwtHelper.getRefreshKey();
        RefreshToken refreshToken = new RefreshToken(subject, LocalDateTime.now(), expiration, refreshKey);
        refreshTokenDao.save(refreshToken);
        return refreshToken;
    }

    //generate access token
    public String generateAccessToken(String refreshTokenValue) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(jwtHelper.getRefreshKey()).build()
                    .parseClaimsJws(refreshTokenValue);
            String jti = (String)claimsJws.getBody().get("jti");
            Optional<RefreshToken> refreshToken = refreshTokenDao.findRefreshTokenByJti(jti);

            if (!refreshToken.isPresent()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }

            RefreshToken token = refreshToken.get();

            LocalDateTime expiration = token.getExpiration();
            if (LocalDateTime.now().isAfter(expiration)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Refresh token expired");
            }

            ApplicationUser user = (ApplicationUser) applicationUserService.loadUserByUsername(token.getSubject());
            String accessToken = jwtHelper.generateAccessToken(user.getUsername(), user.getAuthorities());

            return accessToken;
        } catch (JwtException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

}
