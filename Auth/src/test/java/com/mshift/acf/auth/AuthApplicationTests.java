package com.mshift.acf.auth;

import com.mshift.acf.auth.application_user.ApplicationUserController;
import com.mshift.acf.auth.application_user.ApplicationUserService;
import com.mshift.acf.auth.jwt.JwtHelper;
import com.mshift.acf.auth.jwt.refresh_token.RefreshToken;
import com.mshift.acf.auth.jwt.refresh_token.RefreshTokenDao;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthApplicationTests {

	@Autowired
	private ApplicationUserController applicationUserController;
	@Autowired
	private ApplicationUserService applicationUserService;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private JwtHelper jwtHelper;
	@Autowired
	private RefreshTokenDao refreshTokenDao;

	private static final String testUsername = "test@email.com";
	private static final String testPassword = "test1234";

	@Test
	void contextLoads() {
		assertThat(applicationUserController).isNotNull();
	}

	@Test
	@Order(1)
	void createUser() throws Exception {

		applicationUserService.deleteUser(testUsername);
		JSONObject object = new JSONObject();
		object.put("username", testUsername);
		object.put("password", testPassword);

		MvcResult result = mockMvc.perform(post("/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(object.toString()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.dateCreated").isNotEmpty()).andReturn();


		String refreshToken = result.getResponse().getHeader(jwtHelper.getRefreshTokenHeader());
		String accessToken = result.getResponse().getHeader(jwtHelper.getAuthorizationHeader());

		assertThat(refreshToken).isNotEmpty();
		assertThat(accessToken).isNotEmpty();
	}


	@Test
	@Order(2)
	void emailAlreadyExist() throws Exception {

		JSONObject object = new JSONObject();
		object.put("username", testUsername);
		object.put("password", testPassword);

		mockMvc.perform(post("/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(object.toString()))
				.andDo(print())
				.andExpect(status().isConflict());
	}


	@Test
	void usernameNotEmail() throws Exception {

		String testUsername = "abc";

		JSONObject object = new JSONObject();
		object.put("username", testUsername);
		object.put("password", testPassword);

		mockMvc.perform(post("/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(object.toString()))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	void passwordTooShort() throws Exception {

		String testUsername = "abc@abo.com";
		String testPassword = "1234";

		JSONObject object = new JSONObject();
		object.put("username",testUsername);
		object.put("password",testPassword);

		mockMvc.perform(post("/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(object.toString()))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(2)
	void loginSuccessful() throws Exception {

		JSONObject object = new JSONObject();
		object.put("username",testUsername);
		object.put("password",testPassword);

		mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(object.toString()))
				.andDo(print())
				.andExpect(status().isOk());
	}


	@Test
	@Order(2)
	void loginUnsuccessful() throws Exception {

		String testPassword = "badPassword";

		JSONObject object = new JSONObject();
		object.put("username",testUsername);
		object.put("password",testPassword);

		mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(object.toString()))
				.andDo(print())
				.andExpect(status().isUnauthorized());
	}

	@Test
	@Order(2)
	void logoutAndClearRefreshToken() throws Exception {

		//login first
		JSONObject object = new JSONObject();
		object.put("username",testUsername);
		object.put("password",testPassword);

		MvcResult result = mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(object.toString()))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		String refreshToken = result.getResponse().getHeader(jwtHelper.getRefreshTokenHeader());
		String accessToken = result.getResponse().getHeader(jwtHelper.getAuthorizationHeader());

		assertThat(refreshToken).isNotEmpty();
		assertThat(accessToken).isNotEmpty();

		//then logout

		mockMvc.perform(get("/logout")
				.header(jwtHelper.getRefreshTokenHeader(), refreshToken)
				.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());


		Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(jwtHelper.getRefreshKey()).build()
				.parseClaimsJws(refreshToken.replace(jwtHelper.getTokenPrefix(),""));
		String jti = (String) claimsJws.getBody().get("jti");

		assertThat(jti).isNotEmpty();

		Optional<RefreshToken> refreshTokenByJti = refreshTokenDao.findRefreshTokenByJti(jti);
		assertThat(refreshTokenByJti).isNotPresent();
	}


	@Test
	@Order(2)
	void generateAccessTokenWithRefreshToken() throws Exception {

		//login
		JSONObject object = new JSONObject();
		object.put("username",testUsername);
		object.put("password",testPassword);

		MvcResult result = mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(object.toString()))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		//get refresh token
		String refreshToken = result.getResponse().getHeader(jwtHelper.getRefreshTokenHeader());
		assertThat(refreshToken).isNotEmpty();

		//pass refresh token to token function
		MvcResult refreshResult = mockMvc.perform(get("/token")
				.header(jwtHelper.getRefreshTokenHeader(), refreshToken)
				.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		//get new token
		String newAccessToken = refreshResult.getResponse().getHeader(jwtHelper.getAuthorizationHeader());
		assertThat(newAccessToken).isNotEmpty();

	}

}
