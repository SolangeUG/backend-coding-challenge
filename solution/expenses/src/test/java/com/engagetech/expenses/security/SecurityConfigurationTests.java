package com.engagetech.expenses.security;

import com.engagetech.expenses.ExpensesApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Basic Security Configuration Tests
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = ExpensesApplication.class)
public class SecurityConfigurationTests {

    @LocalServerPort
    private int port;

    @Value("${server.address}")
    private String hostname;

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    private URL baseURL;
    private TestRestTemplate template;

    @Before
    public void setUp() throws MalformedURLException {
        baseURL = new URL("http://" + hostname + ":" + port + "/");
        template = new TestRestTemplate(username, password);
    }

    @Test
    @DisplayName("A user with correct credentials should be authorized to access the API")
    public void shouldAuthorizeUserWithCorrectCredentials() {
        ResponseEntity<String> response =
                template.getForEntity(baseURL.toString(), String.class, port);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("A user with the bad credentials should be denied access")
    public void shouldRefuseAccessToUserWithBadCredentials() {
        template = new TestRestTemplate(
                "unauthorized_user", "unauthorized_password");
        ResponseEntity<String> response =
                template.getForEntity(baseURL.toString(), String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

}
