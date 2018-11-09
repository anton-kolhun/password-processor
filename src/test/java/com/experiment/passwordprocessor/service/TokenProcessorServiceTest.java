package com.experiment.passwordprocessor.service;

import com.experiment.passwordprocessor.dal.entity.PasswordData;
import com.experiment.passwordprocessor.dal.entity.PasswordEntryEntity;
import com.experiment.passwordprocessor.dal.entity.TokenData;
import com.experiment.passwordprocessor.dal.entity.TokenInfoEntity;
import com.experiment.passwordprocessor.dal.entity.TokenInfoHistoryEntity;
import com.experiment.passwordprocessor.dal.repository.TokenInfoHistoryRepository;
import com.experiment.passwordprocessor.dal.repository.TokenInfoRepository;
import com.experiment.passwordprocessor.exception.ObjectNotFoundException;
import com.experiment.passwordprocessor.service.dto.RequesterDto;
import com.experiment.passwordprocessor.service.dto.TokenInfoDto;
import com.experiment.passwordprocessor.service.helper.TokenGenerationHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class TokenProcessorServiceTest {

    @InjectMocks
    private TokenProcessorService tokenProcessorService;

    @Mock
    private TokenInfoRepository tokenInfoRepository;

    @Mock
    private TokenInfoHistoryRepository historyRepository;

    @Mock
    private TokenGenerationHelper tokenGenerationHelper;


    @Test
    public void shouldSavePassword() {
        //setup
        String testToken = "test-token";
        String password = "test-password";
        TokenInfoEntity entity = new TokenInfoEntity();
        entity.setTokenData(TokenData.builder()
                .body(testToken)
                .build());
        PasswordEntryEntity entryEntity = new PasswordEntryEntity();
        entryEntity.setPasswordData(PasswordData.builder()
                .password(password)
                .build());
        entity.setPasswordEntries(Collections.singletonList(entryEntity));
        when(tokenInfoRepository.save(any(TokenInfoEntity.class))).thenReturn(entity);

        //execute
        TokenInfoDto result = tokenProcessorService.savePassword(new TokenInfoDto(), "");

        //verify
        verify(tokenInfoRepository).save(any(TokenInfoEntity.class));
        verify(historyRepository).save(any(TokenInfoHistoryEntity.class));
        assertEquals(testToken, result.getToken());
        assertEquals(password, result.getPasswords().get(0));
    }


    @Test
    public void shouldRequestToken_existing() {
        //setup
        String token = "test-token";
        String password = "test-password";
        TokenInfoEntity tokenInfoEntity = new TokenInfoEntity();
        tokenInfoEntity.setTokenData(TokenData.builder()
                .body(token)
                .build());
        PasswordEntryEntity entryEntity = new PasswordEntryEntity();
        entryEntity.setPasswordData(PasswordData.builder()
                .password(password)
                .build());
        tokenInfoEntity.setPasswordEntries(Collections.singletonList(entryEntity));
        Optional<TokenInfoEntity> tokenInfo = Optional.of(tokenInfoEntity);
        when(tokenInfoRepository.findOneByTokenDataBody(token)).thenReturn(tokenInfo);

        //execute
        TokenInfoDto result = tokenProcessorService.requestToken(token);

        //verify
        verify(tokenInfoRepository).findOneByTokenDataBody(token);
        verify(tokenInfoRepository).delete(tokenInfoEntity);
        verify(historyRepository).save(any(TokenInfoHistoryEntity.class));
        assertEquals(password, result.getPasswords().get(0));

    }


    @Test(expected = ObjectNotFoundException.class)
    public void shouldRequestToken_not_existing() {
        //setup
        String token = "test-token";
        when(tokenInfoRepository.findOneByTokenDataBody(token)).thenReturn(Optional.empty());

        //execute
        TokenInfoDto result = tokenProcessorService.requestToken(token);

        //verify Exception
    }

    @Test
    public void shouldDeleteExpiredTokens() {
        //setup
        String testToken = "expired-token";
        TokenInfoEntity expiredToken = new TokenInfoEntity();
        expiredToken.setTokenData(TokenData.builder()
                .body(testToken)
                .expiredIn(10l)
                .build());
        expiredToken.setCreatedAt(LocalDateTime.now().minusYears(10));
        TokenInfoEntity validToken = new TokenInfoEntity();
        validToken.setCreatedAt(LocalDateTime.now());
        validToken.setTokenData(TokenData.builder()
                .body("valid-token")
                .expiredIn(10l)
                .build());
        List<TokenInfoEntity> tokens = Arrays.asList(expiredToken, validToken);
        when(tokenInfoRepository.findAll()).thenReturn(tokens);

        //execute
        List<TokenInfoDto> deletedTokens = tokenProcessorService.deleteExpiredTokens();

        //verify
        verify(tokenInfoRepository).findAll();
        assertEquals(1, deletedTokens.size());
        verify(historyRepository, times(1)).save(any(TokenInfoHistoryEntity.class));
        verify(tokenInfoRepository).deleteAll(any(List.class));
        assertEquals(testToken, deletedTokens.get(0).getToken());

    }

    @Test
    public void shouldDeleteTokensByRequester() {
        //setup
        String requester = "test-requester";
        RequesterDto requesterDto = new RequesterDto();
        requesterDto.setRequester(requester);
        String token = "test-token";
        TokenInfoEntity tokenInfo = new TokenInfoEntity();
        tokenInfo.setTokenData(TokenData.builder()
                .body(token)
                .build());
        List<TokenInfoEntity> tokens = Arrays.asList(tokenInfo);
        when(tokenInfoRepository
                .findByTokenDataRequesterIgnoreCaseContaining(requester))
                .thenReturn(tokens);
        ArgumentCaptor<List<TokenInfoEntity>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        //execute
        tokenProcessorService.deleteTokensByRequester(requesterDto);

        //verify
        verify(tokenInfoRepository).deleteAll(argumentCaptor.capture());
        assertSame(tokens, argumentCaptor.getValue());
    }
}