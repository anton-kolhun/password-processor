package com.experiment.passwordprocessor.controller;

import com.experiment.passwordprocessor.controller.helper.UrlResolver;
import com.experiment.passwordprocessor.service.TokenService;
import com.experiment.passwordprocessor.service.dto.RequesterDto;
import com.experiment.passwordprocessor.service.dto.TokenInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(PasswordController.PASSWORD_ENDPOINT_URL)
public class PasswordController {

    public static final String PASSWORD_ENDPOINT_URL = "/password";

    @Autowired
    @Qualifier("encryptingTokenProcessorService")
    private TokenService tokenService;

    @Autowired
    private UrlResolver urlResolver;


    @PostMapping
    public TokenInfoDto generateUrlForPassword(@RequestHeader("host") String host,
                                               @RequestHeader(name = "referer", required = false) String referer,
                                               @RequestBody TokenInfoDto tokenInfoDto) {
        String endpointUrl = urlResolver.resolveTokenEndpoint(host);
        tokenInfoDto.setRequester(referer);
        TokenInfoDto updatedDto = tokenService.savePassword(tokenInfoDto, endpointUrl);
        return updatedDto;
    }


    @GetMapping
    public TokenInfoDto fetchPassword(@RequestParam String token) {
        TokenInfoDto tokenInfoDto = tokenService.requestToken(token);
        return tokenInfoDto;
    }

    @PostMapping("delete")
    //@HasRole("admin")
    public List<TokenInfoDto> deleteTokensByRequester(@RequestBody RequesterDto requesterDto) {
        List<TokenInfoDto> removedTokens = tokenService.deleteTokensByRequester(requesterDto);
        return removedTokens;
    }

}
