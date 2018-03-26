package com.engagetech.expenses;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExpensesApplication.class)
public class ExpensesApplicationTests {

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Value("${application.api.path}")
	private String apiPath;

	@Value("${application.api.expenses.endpoint}")
	private String endpoint;

	@Value("${spring.security.user.name}")
	private String username;

	@Value("${spring.security.user.password}")
	private String password;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders
					.webAppContextSetup(webApplicationContext)
					.build();
	}

	@Test
	public void contextLoads() throws Exception {
		MediaType contentType =
				new MediaType(MediaType.APPLICATION_JSON.getType(),
								MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("UTF8"));

		String url = apiPath + endpoint + "/";
		mockMvc.perform(get(url)
				.with(user(username).password(password))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(contentType));
	}
}
