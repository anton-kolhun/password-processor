package com.experiment.passwordprocessor.dal.entity;

import com.experiment.passwordprocessor.dal.enums.PasswordRevisionType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "TOKEN_INFO_HISTORY")
@Entity
@Getter
@Setter
public class TokenInfoHistoryEntity {

    @EmbeddedId
    private HistoryIdentity historyIdentity;

    @Embedded
    private TokenData passwordData;

    @Column(name = "EXECUTION_TIME")
    private LocalDateTime executionTime;

    @OneToMany(mappedBy = "tokenHistoryEntity", cascade = CascadeType.ALL)
    private List<PasswordEntryHistoryEntity> historyPasswordEntries = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        executionTime = LocalDateTime.now();
    }


    @Embeddable
    @Getter
    @Setter
    public static class HistoryIdentity implements Serializable {

        @Column(name = "TOKEN_INFO_ID")
        private String id;

        @Enumerated(EnumType.STRING)
        @Column(name = "REVISION_TYPE")
        private PasswordRevisionType revisionType;
    }

}
