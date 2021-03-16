package com.mshift.acf.user_services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mshift.acf.user_services.user.User;
import com.mshift.acf.user_services.user.UserController;
import com.mshift.acf.user_services.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserServicesApplicationTests {

	@Autowired
	private UserController userController;
	@Autowired
	private UserService userService;
	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
		assertThat(userController).isNotNull();
	}

	@Test
	void createUser() throws Exception {
		User testUser = new User("test@email.com", "test", "account");
		testUser.setDateCreated(null);
		testUser.setDateModified(null);

		try {
			userService.delete(testUser.getUsername());
		} catch (Exception exception){
			// Catching exception when user is not found
		}

		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		String json = mapper.writeValueAsString(testUser);

		mockMvc.perform(
				post("/api/v1/users")
					.contentType(MediaType.APPLICATION_JSON)
					.content(json))
				.andDo(print())
				.andExpect(status().isOk());
	}

}
