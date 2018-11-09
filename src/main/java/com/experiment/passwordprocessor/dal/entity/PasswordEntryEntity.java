package com.experiment.passwordprocessor.dal.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name = "PASSWORD_ENTRY")
@Entity
@Getter
@Setter
public class PasswordEntryEntity {

    @Id
    @Column(name = "PASSWORD_ENTRY_ID")
    @GenericGenerator(name = "uuid-generator", strategy = "uuid2")
    @GeneratedValue(generator = "uuid-generator")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TOKEN_INFO_ID")
    private TokenInfoEntity tokenInfoEntity;

    @Embedded
    private PasswordData passwordData;
}
