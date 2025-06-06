package com.AgeniusAgent.Agenius.intercepteur;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    @Autowired
    private HttpServletRequest request;

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {

        String token = request.getHeader("Authorization");

        if (token != null && !token.isEmpty()) {
            httpRequest.getHeaders().add("Authorization", token);
        }

        return execution.execute(httpRequest, body);
    }
}
