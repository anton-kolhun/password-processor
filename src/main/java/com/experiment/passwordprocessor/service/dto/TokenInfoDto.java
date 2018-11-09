package com.experiment.passwordprocessor.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenInfoDto {

    @JsonIgnore
    private String id;

    private List<String> passwords = new ArrayList<>();

    @JsonIgnore
    private String token;

    @JsonIgnore
    private String requester;

    private String url;

    private Long expiredIn;
}
