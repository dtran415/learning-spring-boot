package com.mshift.acf.api_gateway.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mshift.acf.api_gateway.ApplicationConfig;
import com.mshift.acf.api_gateway.jwt.JwtHelper;
import com.mshift.acf.api_gateway.user.UserConfig;
import com.mshift.acf.api_gateway.user.UserRegistration;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@RestController
public class AuthController {

    private final OkHttpClient client;
    private final JwtHelper jwtHelper;
    private final ObjectMapper objectMapper;

    @Autowired
    public AuthController(OkHttpClient client, JwtHelper jwtHelper, ObjectMapper objectMapper) {
        this.client = client;
        this.jwtHelper = jwtHelper;
        this.objectMapper = objectMapper;
    }

    @PostMapping(path = "login")
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(body, ApplicationConfig.JSON());
        Request authRequest = new Request.Builder()
                .url(AuthConfig.BASE_URL+ "/login")
                .post(requestBody)
                .build();

        Call call = client.newCall(authRequest);
        Response authResponse = call.execute();

        String accessToken = authResponse.header(jwtHelper.getAuthorizationHeader());
        if (accessToken != null) {
            response.setHeader(jwtHelper.getAuthorizationHeader(), accessToken);
        }

        response.setStatus(authResponse.code());
        String authResponseBody = authResponse.body().string();
        response.getWriter().write(authResponseBody);
        response.getWriter().flush();
    }

    @PostMapping(path = "register")
    public void register(@RequestBody UserRegistration userRegistration, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type", "application/json");

        String body = objectMapper.writeValueAsString(userRegistration);
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(body, ApplicationConfig.JSON());
        Request registerRequest = new Request.Builder()
                .url(AuthConfig.BASE_URL+ "/register")
                .post(requestBody)
                .build();

        Call call = client.newCall(registerRequest);
        Response applicationUserRegisterResponse = call.execute();
        int statusCode = applicationUserRegisterResponse.code();
        String registerApplicationUserBody = applicationUserRegisterResponse.body().string();
        // if applicationUser created successfully
        if (statusCode == HttpStatus.OK.value()) {
            // set access and refresh token, so user is automatically logged in when registered
            String accessToken = applicationUserRegisterResponse.header(jwtHelper.getAuthorizationHeader());
            String refreshToken = applicationUserRegisterResponse.header(jwtHelper.getRefreshTokenHeader());
            Response userRegistrationResponse = createUserAtUserService(userRegistration, accessToken);
            String userRegistrationBody = userRegistrationResponse.body().string();

            statusCode = userRegistrationResponse.code();
            response.setStatus(statusCode);
            // if User created successfully
            if (statusCode == HttpStatus.OK.value()) {
                response.setHeader(jwtHelper.getAuthorizationHeader(), accessToken);
                response.setHeader(jwtHelper.getRefreshTokenHeader(), refreshToken);
                response.getWriter().write(userRegistrationBody);
                response.getWriter().flush();
            } else { // if creation failed, go back and delete applicationUser
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                ApplicationUserDTO applicationUserDTO = objectMapper.readValue(registerApplicationUserBody, ApplicationUserDTO.class);
                deleteApplicationUser(applicationUserDTO.getId(), accessToken);
            }
        } else {
            response.setStatus(statusCode);
            response.getWriter().write(registerApplicationUserBody);
            response.getWriter().flush();;
        }
    }

    private void deleteApplicationUser(String id, String accessToken) throws IOException {
        Request registerRequest = new Request.Builder()
                .header(jwtHelper.getAuthorizationHeader(), accessToken)
                .url(AuthConfig.BASE_URL+"/user/" + id)
                .delete()
                .build();

        Call call = client.newCall(registerRequest);
        Response deleteResponse = call.execute();
        if (deleteResponse.code() != HttpStatus.OK.value()) {
            System.out.println("Error deleting user");
        }
    }

    private Response createUserAtUserService(UserRegistration userRegistration, String accessToken) throws IOException {
        String body = objectMapper.writeValueAsString(userRegistration);
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(body, ApplicationConfig.JSON());
        Request registerRequest = new Request.Builder()
                .header(jwtHelper.getAuthorizationHeader(), accessToken)
                .url(UserConfig.BASE_URL)
                .post(requestBody)
                .build();

        Call call = client.newCall(registerRequest);
        Response registerResponse = call.execute();
        return registerResponse;
    }

}
