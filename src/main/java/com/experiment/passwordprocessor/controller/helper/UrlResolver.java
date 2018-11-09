package com.experiment.passwordprocessor.controller.helper;

import com.experiment.passwordprocessor.controller.PasswordController;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Component;

@Component
public class UrlResolver {

    private final String appContextPath;

    public UrlResolver(ServerProperties serverProperties) {
        /*Integer port = serverProperties.getPort();
        if (port == null) {
            port = 8080;
        }*/
        String contextPath = serverProperties.getServlet().getContextPath();
        if (StringUtils.isEmpty(contextPath)) {
            contextPath = "";
        }
        appContextPath = contextPath;
    }


    public String resolveTokenEndpoint(String host) {
        return "http://" + host + appContextPath +
                PasswordController.PASSWORD_ENDPOINT_URL
                + "?token=";
    }

}
