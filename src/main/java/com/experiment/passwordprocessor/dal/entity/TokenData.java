package com.experiment.passwordprocessor.dal.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;

@Data
@Builder
public class TokenData {

    @Column(name = "TOKEN")
    private String body;

    @Column(name = "EXPIRED_IN")
    private Long expiredIn;

    @Column(name = "REQUESTER")
    private String requester;
}
