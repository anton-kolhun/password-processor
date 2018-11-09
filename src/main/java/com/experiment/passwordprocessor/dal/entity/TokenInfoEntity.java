package com.experiment.passwordprocessor.dal.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "TOKEN_INFO")
@Entity
@Getter
@Setter
public class TokenInfoEntity {

    @Id
    @Column(name = "TOKEN_INFO_ID")
    @GenericGenerator(name = "uuid-generator", strategy = "uuid2")
    @GeneratedValue(generator = "uuid-generator")
    private String id;

    @Embedded
    private TokenData tokenData;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "tokenInfoEntity", cascade = CascadeType.ALL)
    private List<PasswordEntryEntity> passwordEntries = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public void setPasswordEntries(List<PasswordEntryEntity> passwordEntries) {
        this.passwordEntries.clear();
        this.passwordEntries.addAll(passwordEntries);
    }
}
