package com.experiment.passwordprocessor.dal.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name = "PASSWORD_ENTRY_HISTORY")
@Entity
@Getter
@Setter
public class PasswordEntryHistoryEntity {

    @Id
    @Column(name = "PASSWORD_ENTRY_HISTORY_ID")
    @GenericGenerator(name = "uuid-generator", strategy = "uuid2")
    @GeneratedValue(generator = "uuid-generator")
    private String id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "TOKEN_INFO_ID", referencedColumnName = "TOKEN_INFO_ID"),
            @JoinColumn(name = "REVISION_TYPE", referencedColumnName = "REVISION_TYPE")
    })
    private TokenInfoHistoryEntity tokenHistoryEntity;

    @Embedded
    private PasswordData passwordData;
}
