package com.vsp.api.service;

import java.util.Arrays;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public abstract class BaseService {

    private static final Logger logger = LoggerFactory.getLogger(BaseService.class);

    protected static final String AUTHORIZATION_TYPE_BEARER = "bearer";
    private static final String CLIENT_CREDENTIALS = "client_credentials";

    private static final String OAUTH_CLIENT_SCOPES = "oauth.client_scopes";
    private static final String OAUTH_CLIENT_ID = "oauth.client_id";
    private static final String OAUTH_CLIENT_SECRET = "oauth.client_secret";
    private static final String OAUTH_URI = "oauth.resource_uri";

    @Autowired
    @Qualifier("productUpdateRestTemplate")
    protected RestTemplate restTemplate;

    @Autowired
    protected Environment environment;

    protected String getToken() {
        String client_id = environment.getProperty(OAUTH_CLIENT_ID);
        String client_secret = environment.getProperty(OAUTH_CLIENT_SECRET);
        String client_scope = environment.getProperty(OAUTH_CLIENT_SCOPES);
        String uri = environment.getProperty(OAUTH_URI);
        HttpHeaders headers = new HttpHeaders();

        if (StringUtils.isBlank(client_secret)) {
            throw new RuntimeException("client_secret has not been set!");
        }

        headers.setAccept(Arrays.asList(org.springframework.http.MediaType.APPLICATION_JSON));
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> mapParams = getOAuthParameters(client_id, client_secret, client_scope);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(mapParams, headers);
        ResponseEntity<LinkedHashMap> response = restTemplate.exchange(uri, HttpMethod.POST, entity,
                LinkedHashMap.class);
        String token = (String) response.getBody().get("access_token");

        return token;
    }

    private MultiValueMap<String, String> getOAuthParameters(String client_id, String client_secret, String scope) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

        headers.add("grant_type", CLIENT_CREDENTIALS);
        headers.add("scope", scope);
        headers.add("client_id", client_id);
        headers.add("client_secret", client_secret);

        return headers;
    }

    protected HttpEntity<?> buildGETHttpRequestWithAcceptVersion(String version) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE + ";version=" + version);
        headers.add("Authorization", AUTHORIZATION_TYPE_BEARER + " " + getToken());
        return new HttpEntity<>(headers);
    }

    protected HttpEntity<?> buildGETHttpRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", AUTHORIZATION_TYPE_BEARER + " " + getToken());
        return new HttpEntity<>(headers);
    }

    protected String getResourceURL(String UriProperty) {
        String resourceURL = "";
        try {
            resourceURL = environment.getProperty(UriProperty);
        } catch (Exception e) {
            logger.error("BaseService : exception in getResourceURL() for " + UriProperty, e);
        }
        return resourceURL;
    }


}
