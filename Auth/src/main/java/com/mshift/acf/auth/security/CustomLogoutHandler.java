package com.mshift.acf.auth.security;

import com.google.common.base.Strings;
import com.mshift.acf.auth.jwt.JwtHelper;
import com.mshift.acf.auth.jwt.refresh_token.RefreshTokenDao;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomLogoutHandler implements LogoutHandler {

    private final JwtHelper jwtHelper;
    private final RefreshTokenDao refreshTokenDao;

    @Autowired
    public CustomLogoutHandler(JwtHelper jwtHelper, RefreshTokenDao refreshTokenDao) {
        this.jwtHelper = jwtHelper;
        this.refreshTokenDao = refreshTokenDao;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String refreshTokenValue = request.getHeader(jwtHelper.getRefreshTokenHeader());

        if (Strings.isNullOrEmpty(refreshTokenValue))
            return;

        refreshTokenValue = refreshTokenValue.replace(jwtHelper.getTokenPrefix(), "");
        invalidateRefreshToken(refreshTokenValue);
    }

    private void invalidateRefreshToken(String refreshTokenValue) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(jwtHelper.getRefreshKey()).build()
                    .parseClaimsJws(refreshTokenValue);
            String jti = (String) claimsJws.getBody().get("jti");
            if (Strings.isNullOrEmpty(jti))
                return;

            refreshTokenDao.deleteByJti(jti);
        } catch (JwtException e) {

        }
    }
}
