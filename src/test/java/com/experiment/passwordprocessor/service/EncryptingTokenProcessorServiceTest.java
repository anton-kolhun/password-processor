package com.experiment.passwordprocessor.service;

import com.experiment.passwordprocessor.service.dto.RequesterDto;
import com.experiment.passwordprocessor.service.dto.TokenInfoDto;
import com.experiment.passwordprocessor.service.helper.DataEncryptionHelper;
import com.experiment.passwordprocessor.service.helper.TokenGenerationHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EncryptingTokenProcessorServiceTest {

    @InjectMocks
    private EncryptingTokenProcessorService encryptingTokenProcessorService;

    @Mock
    private TokenProcessorService tokenProcessorService;

    @Mock
    private DataEncryptionHelper dataEncryptionHelper;

    @Mock
    private TokenGenerationHelper tokenGenerationHelper;

    @Test
    public void shouldSavePassword() {
        //setup
        String password = "test-password";
        TokenInfoDto tokenInfoDto = createTokenInfoDto(password);
        String url = "test-url";
        String encryptedPassword = "encrypted-password";
        String token = "test-token";
        TokenInfoDto encryptedDto = createTokenInfoDto(encryptedPassword, token);
        when(tokenProcessorService.savePassword(any(TokenInfoDto.class), any(String.class)))
                .thenReturn(encryptedDto);
        String decryptedPassword = "decrypted-password";
        when(dataEncryptionHelper.decrypt(encryptedPassword))
                .thenReturn(decryptedPassword);
        String encryptedToken = "encrypted-token";
        when(dataEncryptionHelper.encrypt(token)).thenReturn(encryptedToken);

        //execute
        TokenInfoDto result = encryptingTokenProcessorService.savePassword(tokenInfoDto, url);

        //verify
        verify(dataEncryptionHelper).encrypt(password);
        verify(tokenProcessorService).savePassword(any(TokenInfoDto.class), any(String.class));
        verify(dataEncryptionHelper).decrypt(encryptedPassword);
        verify(dataEncryptionHelper).encrypt(token);
        assertEquals(result.getPasswords().get(0), decryptedPassword);
        assertEquals(result.getToken(), encryptedToken);

    }


    @Test
    public void shouldRequestToken() {
        //setup
        String encryptedToken = "encrypted-token";
        String decryptedToken = "decrypted-token";
        when(dataEncryptionHelper.decrypt(encryptedToken)).thenReturn(decryptedToken);
        String encryptedPassword = "encrypted-password";
        TokenInfoDto encryptedDto = createTokenInfoDto(encryptedPassword);
        when(tokenProcessorService.requestToken(decryptedToken)).thenReturn(encryptedDto);
        String decryptedPassword = "decrypted-password";
        when(dataEncryptionHelper.decrypt(encryptedPassword))
                .thenReturn(decryptedPassword);

        //execute
        TokenInfoDto result = encryptingTokenProcessorService.requestToken(encryptedToken);

        //verify
        verify(dataEncryptionHelper).decrypt(encryptedToken);
        verify(tokenProcessorService).requestToken(decryptedToken);
        verify(dataEncryptionHelper).decrypt(encryptedPassword);
        assertEquals(result.getPasswords().get(0), decryptedPassword);
    }

    @Test
    public void shouldDeleteTokensByRequester() {
        //setup
        RequesterDto requesterDto = new RequesterDto();
        requesterDto.setRequester("test-requester");
        String encryptedPassword = "encrypted-password";
        TokenInfoDto encryptedDto = createTokenInfoDto(encryptedPassword);
        when(tokenProcessorService.deleteTokensByRequester(requesterDto))
                .thenReturn(Collections.singletonList(encryptedDto));
        String decryptedPassword = "decrypted-password";
        when(dataEncryptionHelper.decrypt(encryptedPassword))
                .thenReturn(decryptedPassword);

        //execute
        List<TokenInfoDto> result = encryptingTokenProcessorService.deleteTokensByRequester(requesterDto);

        //verify
        verify(tokenProcessorService).deleteTokensByRequester(requesterDto);
        verify(dataEncryptionHelper).decrypt(encryptedPassword);
        assertEquals(1, result.size());
        assertEquals(result.get(0).getPasswords().get(0), decryptedPassword);

    }

    private TokenInfoDto createTokenInfoDto(String encryptedPassword) {
        TokenInfoDto encryptedDto = new TokenInfoDto();
        encryptedDto.setPasswords(Collections.singletonList(encryptedPassword));
        return encryptedDto;
    }

    private TokenInfoDto createTokenInfoDto(String encryptedPassword, String token) {
        TokenInfoDto encryptedDto = createTokenInfoDto(encryptedPassword);
        encryptedDto.setToken(token);
        return encryptedDto;
    }
}