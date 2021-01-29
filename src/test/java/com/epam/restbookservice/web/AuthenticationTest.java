package com.epam.restbookservice.web;

import com.epam.restbookservice.domain.User;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationTest {

    public static final String BASE_URL = "http://localhost:%d/auth/";
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void signInShouldReturnToken() throws Exception {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var loginJsonObject = new JSONObject();
        loginJsonObject.put("username", "user");
        loginJsonObject.put("password", "user");

        assertThat(this.restTemplate.postForObject(
                String.format(BASE_URL + "signin", port),
                new HttpEntity<>(loginJsonObject.toString(), headers),
                String.class))
                .containsPattern("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$");
    }

    @Test
    public void signUpShouldReturnUser() throws Exception  {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var loginJsonObject = new JSONObject();
        loginJsonObject.put("username", "testuser");
        loginJsonObject.put("password", "testuser");
        loginJsonObject.put("firstName", "Test");
        loginJsonObject.put("lastName", "User");


        assertThat(this.restTemplate.postForObject(
                String.format(BASE_URL + "signup", port),
                new HttpEntity<>(loginJsonObject.toString(), headers),
                User.class))
                .hasFieldOrPropertyWithValue("username", "testuser")
                .hasFieldOrPropertyWithValue("firstName", "Test")
                .hasFieldOrPropertyWithValue("lastName", "User");

    }
}
