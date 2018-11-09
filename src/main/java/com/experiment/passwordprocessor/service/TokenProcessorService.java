package com.experiment.passwordprocessor.service;

import com.experiment.passwordprocessor.dal.entity.PasswordData;
import com.experiment.passwordprocessor.dal.entity.PasswordEntryEntity;
import com.experiment.passwordprocessor.dal.entity.PasswordEntryHistoryEntity;
import com.experiment.passwordprocessor.dal.entity.TokenData;
import com.experiment.passwordprocessor.dal.entity.TokenInfoEntity;
import com.experiment.passwordprocessor.dal.entity.TokenInfoHistoryEntity;
import com.experiment.passwordprocessor.dal.entity.TokenInfoHistoryEntity.HistoryIdentity;
import com.experiment.passwordprocessor.dal.enums.PasswordRevisionType;
import com.experiment.passwordprocessor.dal.repository.TokenInfoHistoryRepository;
import com.experiment.passwordprocessor.dal.repository.TokenInfoRepository;
import com.experiment.passwordprocessor.exception.ObjectNotFoundException;
import com.experiment.passwordprocessor.service.dto.RequesterDto;
import com.experiment.passwordprocessor.service.dto.TokenInfoDto;
import com.experiment.passwordprocessor.service.helper.TokenGenerationHelper;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class TokenProcessorService implements TokenService {

    private final TokenInfoRepository tokenInfoRepository;

    private final TokenInfoHistoryRepository historyRepository;

    private final TokenGenerationHelper tokenGenerationHelper;

    private final Long defaultTokenDuration; // duration in seconds


    @Override
    @Transactional
    public TokenInfoDto savePassword(TokenInfoDto passwordInfoDto, String endpointUrl) {
        //validate()
        applyDefaults(passwordInfoDto);
        TokenInfoEntity tokenEntity = toEntity(passwordInfoDto);
        tokenEntity = tokenInfoRepository.save(tokenEntity);
        createHistoryRecord(tokenEntity, PasswordRevisionType.CREATED);
        return toDto(tokenEntity, endpointUrl);
    }

    private void applyDefaults(TokenInfoDto passwordInfoDto) {
        if (passwordInfoDto.getExpiredIn() == null) {
            passwordInfoDto.setExpiredIn(defaultTokenDuration);
        }
    }


    @Override
    @Transactional
    public TokenInfoDto requestToken(String token) {
        TokenInfoEntity foundEntity = tokenInfoRepository
                .findOneByTokenDataBody(token)
                .orElseThrow(
                        () -> new ObjectNotFoundException("there were no passwords generated for then given token"));
        tokenInfoRepository.delete(foundEntity);
        validate(foundEntity);
        createHistoryRecord(foundEntity, PasswordRevisionType.REQUESTED);
        return toDto(foundEntity);
    }

    @Override
    @Transactional
    public List<TokenInfoDto> deleteExpiredTokens() {
        List<TokenInfoEntity> tokens = tokenInfoRepository.findAll();

        List<TokenInfoEntity> expiredEntites = tokens.stream()
                .filter(tokenInfoEntity -> {
                    if (tokenInfoEntity.getTokenData().getExpiredIn() != null) {
                        LocalDateTime expiredAt = tokenInfoEntity.getCreatedAt()
                                .plusSeconds(tokenInfoEntity.getTokenData().getExpiredIn());
                        if (expiredAt.isBefore(LocalDateTime.now())) {
                            createHistoryRecord(tokenInfoEntity, PasswordRevisionType.EXPIRED);
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
        tokenInfoRepository.deleteAll(expiredEntites);
        return expiredEntites.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


    @Transactional
    public List<TokenInfoDto> deleteTokensByRequester(RequesterDto requesterDto) {
        List<TokenInfoEntity> matchingTokens = tokenInfoRepository
                .findByTokenDataRequesterIgnoreCaseContaining(requesterDto.getRequester());
        tokenInfoRepository.deleteAll(matchingTokens);
        return matchingTokens.stream()
                .map(passwordInfoEntity -> {
                    createHistoryRecord(passwordInfoEntity, PasswordRevisionType.DELETED);
                    return toDto(passwordInfoEntity);
                })
                .collect(Collectors.toList());
    }

    private void validate(TokenInfoEntity foundEntity) {
        if (foundEntity.getTokenData().getExpiredIn() != null) {
            LocalDateTime expiredAt = foundEntity.getCreatedAt()
                    .plusSeconds(foundEntity.getTokenData().getExpiredIn());
            if (expiredAt.isBefore(LocalDateTime.now())) {
                createHistoryRecord(foundEntity, PasswordRevisionType.EXPIRED);
                throw new ObjectNotFoundException("there were no passwords generated for the given token");
            }
        }
    }

    private void createHistoryRecord(TokenInfoEntity foundEntity, PasswordRevisionType revisionType) {
        HistoryIdentity historyIdentity = new HistoryIdentity();
        historyIdentity.setId(foundEntity.getId());
        historyIdentity.setRevisionType(revisionType);
        TokenInfoHistoryEntity tokenInfoHistoryEntity = new TokenInfoHistoryEntity();
        tokenInfoHistoryEntity.setHistoryIdentity(historyIdentity);
        tokenInfoHistoryEntity.setPasswordData(foundEntity.getTokenData());
        List<PasswordEntryHistoryEntity> historyEntries = foundEntity.getPasswordEntries().stream()
                .map(passwordEntryEntity -> {
                    PasswordEntryHistoryEntity historyEntry = new PasswordEntryHistoryEntity();
                    historyEntry.setTokenHistoryEntity(tokenInfoHistoryEntity);
                    historyEntry.setPasswordData(passwordEntryEntity.getPasswordData());
                    return historyEntry;
                })
                .collect(Collectors.toList());
        tokenInfoHistoryEntity.setHistoryPasswordEntries(historyEntries);
        historyRepository.save(tokenInfoHistoryEntity);
    }

    private TokenInfoEntity toEntity(TokenInfoDto passwordInfoDto) {
        TokenInfoEntity tokenInfoEntity = new TokenInfoEntity();
        List<String> passwords = passwordInfoDto.getPasswords();
        List<PasswordEntryEntity> entryEntities = new ArrayList<>();
        for (int i = 0; i < passwords.size(); i++) {
            String password = passwords.get(i);
            PasswordEntryEntity entryEntity = new PasswordEntryEntity();
            PasswordData passwordData = PasswordData.builder()
                    .password(password)
                    .seqNumber(i + 1)
                    .build();
            entryEntity.setPasswordData(passwordData);
            entryEntity.setTokenInfoEntity(tokenInfoEntity);
            entryEntities.add(entryEntity);
        }
        tokenInfoEntity.setPasswordEntries(entryEntities);
        TokenData tokenData = TokenData.builder()
                .body(tokenGenerationHelper.generateToken())
                .expiredIn(passwordInfoDto.getExpiredIn())
                .requester(passwordInfoDto.getRequester())
                .build();
        tokenInfoEntity.setTokenData(tokenData);
        return tokenInfoEntity;
    }


    private TokenInfoDto toDto(TokenInfoEntity passwordInfoEntity, String endpointUrl) {
        TokenInfoDto passwordInfoDto = toDto(passwordInfoEntity);
        String url = tokenGenerationHelper.resolveUrl(endpointUrl, passwordInfoEntity.getTokenData().getBody());
        passwordInfoDto.setUrl(url);
        return passwordInfoDto;
    }

    private TokenInfoDto toDto(TokenInfoEntity passwordInfoEntity) {
        TokenInfoDto passwordInfoDto = new TokenInfoDto();
        passwordInfoDto.setId(passwordInfoEntity.getId());
        List<String> passwords = passwordInfoEntity.getPasswordEntries().stream()
                .sorted(Comparator.comparing(passwordEntryEntity -> passwordEntryEntity
                        .getPasswordData().getSeqNumber()))
                .map(passwordEntryEntity -> passwordEntryEntity
                        .getPasswordData().getPassword())
                .collect(Collectors.toList());

        passwordInfoDto.setPasswords(passwords);
        passwordInfoDto.setToken(passwordInfoEntity.getTokenData().getBody());
        passwordInfoDto.setExpiredIn(passwordInfoEntity.getTokenData().getExpiredIn());
        passwordInfoDto.setRequester(passwordInfoEntity.getTokenData().getRequester());
        return passwordInfoDto;
    }
}
