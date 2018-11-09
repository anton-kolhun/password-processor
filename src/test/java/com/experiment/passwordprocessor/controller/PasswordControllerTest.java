package com.experiment.passwordprocessor.controller;

import com.experiment.passwordprocessor.PasswordProcessorApplication;
import com.experiment.passwordprocessor.config.TestDaoConfig;
import com.experiment.passwordprocessor.service.dto.TokenInfoDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {PasswordProcessorApplication.class, TestDaoConfig.class})
public class PasswordControllerTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    public void shouldGenerateToken() throws Exception {
        //setup
        String password1 = "test-password1";
        String password2 = "test-password2";
        String payload = "{\"passwords\":[\"" + password1 + "\",\"" + password2 + "\"]}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);

        //execute
        ResponseEntity<TokenInfoDto> createdTokenResponse = restTemplate.exchange("http://localhost:"
                        + port + PasswordController.PASSWORD_ENDPOINT_URL,
                HttpMethod.POST, requestEntity, TokenInfoDto.class);

        ResponseEntity<String> fetchPasswordResponse = restTemplate.exchange(new URI(createdTokenResponse.getBody().getUrl()),
                HttpMethod.GET, null, String.class);

        //verify
        assertEquals(HttpStatus.OK, createdTokenResponse.getStatusCode());
        assertTrue(fetchPasswordResponse.getBody().contains(password1));
        assertTrue(fetchPasswordResponse.getBody().contains(password2));


    }


    @Test
    public void shouldNotFetchPasswordMoreThanOnce() throws Exception {
        //setup
        String password1 = "test-password1";
        String password2 = "test-password2";
        String payload = "{\"passwords\":[\"" + password1 + "\",\"" + password2 + "\"]}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);

        //execute
        ResponseEntity<TokenInfoDto> createdTokenResponse = restTemplate.exchange("http://localhost:"
                        + port + PasswordController.PASSWORD_ENDPOINT_URL,
                HttpMethod.POST, requestEntity, TokenInfoDto.class);
        ResponseEntity<String> firstTryResponse = restTemplate.exchange(new URI(createdTokenResponse.getBody().getUrl()),
                HttpMethod.GET, null, String.class);
        ResponseEntity<String> secondTryResponse = restTemplate.exchange(new URI(createdTokenResponse.getBody().getUrl()),
                HttpMethod.GET, null, String.class);

        //verify
        assertEquals(HttpStatus.OK, firstTryResponse.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, secondTryResponse.getStatusCode());

    }

    @Test
    public void shouldDeleteTokensByRequester() throws Exception {
        //setup
        String password1 = "test-password1";
        String password2 = "test-password2";
        String payload = "{\"passwords\":[\"" + password1 + "\",\"" + password2 + "\"]}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requester = "it-referer";
        headers.add("referer", requester);
        HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);

        //execute
        ResponseEntity<TokenInfoDto> createdTokenResponse = restTemplate.exchange("http://localhost:"
                        + port + PasswordController.PASSWORD_ENDPOINT_URL,
                HttpMethod.POST, requestEntity, TokenInfoDto.class);

        payload = "{\"requester\":\"" + requester + "\"}";
        requestEntity = new HttpEntity<>(payload, headers);
        TokenInfoDto[] removedTokens = restTemplate.exchange("http://localhost:"
                        + port + PasswordController.PASSWORD_ENDPOINT_URL + "/delete",
                HttpMethod.POST, requestEntity, TokenInfoDto[].class).getBody();

        ResponseEntity<String> fetchResponse = restTemplate.exchange(new URI(createdTokenResponse.getBody().getUrl()),
                HttpMethod.GET, null, String.class);

        //verify
        assertEquals(1, removedTokens.length);
        assertTrue(removedTokens[0].getPasswords().contains(password1));
        assertTrue(removedTokens[0].getPasswords().contains(password2));
        assertEquals(HttpStatus.NOT_FOUND, fetchResponse.getStatusCode());

    }

}