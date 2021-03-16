package com.mshift.acf.api_gateway;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mshift.acf.api_gateway.auth.AuthController;
import com.mshift.acf.api_gateway.user.UserRegistration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiGatewayApplicationTests {

	@Autowired
	private AuthController authController;
	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
		assertThat(authController).isNotNull();
	}

	@Test
	void registerNewUser() throws Exception{

		String testUsername = "test@email.com";
		String testPassword = "test1234";
		String testFirstName = "test";
		String testLastName = "account";

		UserRegistration userRegistration = new UserRegistration(testFirstName, testLastName, testUsername, testPassword);

		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		String json = mapper.writeValueAsString(userRegistration);

		mockMvc.perform(post("/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.dateCreated").isNotEmpty());

	}

}
