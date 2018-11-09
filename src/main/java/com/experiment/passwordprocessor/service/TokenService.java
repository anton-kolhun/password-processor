package com.experiment.passwordprocessor.service;

import com.experiment.passwordprocessor.service.dto.RequesterDto;
import com.experiment.passwordprocessor.service.dto.TokenInfoDto;

import java.util.List;

public interface TokenService {

    TokenInfoDto savePassword(TokenInfoDto passwordInfoDto, String endpointUrl);

    TokenInfoDto requestToken(String token);

    List<TokenInfoDto> deleteExpiredTokens();

    List<TokenInfoDto> deleteTokensByRequester(RequesterDto requesterDto);
}
