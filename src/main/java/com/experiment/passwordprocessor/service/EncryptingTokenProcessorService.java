package com.experiment.passwordprocessor.service;


import com.experiment.passwordprocessor.service.dto.RequesterDto;
import com.experiment.passwordprocessor.service.dto.TokenInfoDto;
import com.experiment.passwordprocessor.service.helper.DataEncryptionHelper;
import com.experiment.passwordprocessor.service.helper.TokenGenerationHelper;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EncryptingTokenProcessorService implements TokenService {

    @Delegate(excludes = EncryptionAware.class)
    private final TokenProcessorService tokenProcessorService;

    private final TokenGenerationHelper tokenGenerationHelper;

    private final DataEncryptionHelper dataEncryptionHelper;

    @Override
    public TokenInfoDto savePassword(TokenInfoDto passwordInfoDto, String endpointUrl) {
        TokenInfoDto encryptedDto = covertDto(passwordInfoDto, dataEncryptionHelper::encrypt);
        TokenInfoDto saved = tokenProcessorService.savePassword(encryptedDto, endpointUrl);
        TokenInfoDto result = covertDto(saved, dataEncryptionHelper::decrypt);
        encryptToken(endpointUrl, result);
        return result;
    }

    @Override
    public TokenInfoDto requestToken(String token) {
        String decryptedToken = dataEncryptionHelper.decrypt(token);
        TokenInfoDto encryptedDto = tokenProcessorService.requestToken(decryptedToken);
        return covertDto(encryptedDto, dataEncryptionHelper::decrypt);

    }

    @Override
    public List<TokenInfoDto> deleteTokensByRequester(RequesterDto requester) {
        List<TokenInfoDto> encryptedTokens = tokenProcessorService.deleteTokensByRequester(requester);
        return encryptedTokens.stream()
                .map(tokenInfoDto -> covertDto(tokenInfoDto, dataEncryptionHelper::decrypt))
                .collect(Collectors.toList());
    }

    private void encryptToken(String endpointUrl, TokenInfoDto result) {
        String encryptedToken = dataEncryptionHelper.encrypt(result.getToken());
        result.setToken(encryptedToken);
        String url = tokenGenerationHelper.resolveUrl(endpointUrl, encryptedToken);
        result.setUrl(url);
    }

    private TokenInfoDto covertDto(TokenInfoDto passwordInfoDto,
                                   Function<String, String> passwordConverter) {
        List<String> encryptedPasswords = passwordInfoDto.getPasswords().stream()
                .map(passwordConverter)
                .collect(Collectors.toList());
        TokenInfoDto encryptedDto = new TokenInfoDto();
        BeanUtils.copyProperties(passwordInfoDto, encryptedDto);
        encryptedDto.setPasswords(encryptedPasswords);
        return encryptedDto;
    }

    private interface EncryptionAware {
        TokenInfoDto requestToken(String token);

        TokenInfoDto savePassword(TokenInfoDto passwordInfoDto, String endpointUrl);

        List<TokenInfoDto> deleteTokensByRequester(RequesterDto requester);

    }
}
