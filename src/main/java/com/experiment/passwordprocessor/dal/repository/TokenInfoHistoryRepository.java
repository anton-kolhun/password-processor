package com.experiment.passwordprocessor.dal.repository;

import com.experiment.passwordprocessor.dal.entity.TokenInfoHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenInfoHistoryRepository extends
        JpaRepository<TokenInfoHistoryEntity, TokenInfoHistoryEntity.HistoryIdentity> {
}
