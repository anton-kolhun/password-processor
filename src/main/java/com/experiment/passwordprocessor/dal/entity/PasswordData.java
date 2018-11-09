package com.experiment.passwordprocessor.dal.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;

@Data
@Builder
public class PasswordData {

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "SEQ_NUMBER")
    private Integer seqNumber;
}
