package com.experiment.passwordprocessor.dal.repository;

import com.experiment.passwordprocessor.dal.entity.TokenInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenInfoRepository extends JpaRepository<TokenInfoEntity, String> {

    Optional<TokenInfoEntity> findOneByTokenDataBody(String token);

    List<TokenInfoEntity> findByTokenDataRequesterIgnoreCaseContaining(String requester);
}
