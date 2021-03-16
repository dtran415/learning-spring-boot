package com.mshift.acf.auth.jwt.refresh_token;

import com.google.common.base.Strings;
import com.mshift.acf.auth.jwt.JwtConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;
    private final JwtConfig jwtConfig;

    @Autowired
    public RefreshTokenController(RefreshTokenService refreshTokenService, JwtConfig jwtConfig) {
        this.refreshTokenService = refreshTokenService;
        this.jwtConfig = jwtConfig;
    }

    //get access token
    @GetMapping(path = "token")
    public void getAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = request.getHeader(jwtConfig.getRefreshTokenHeader());
        if (Strings.isNullOrEmpty(refreshToken))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        refreshToken = refreshToken.replace(jwtConfig.getTokenPrefix(), "");

        String accessToken = refreshTokenService.generateAccessToken(refreshToken);
        response.addHeader(jwtConfig.getAuthorizationHeader(), jwtConfig.getTokenPrefix() + accessToken);
    }

}
