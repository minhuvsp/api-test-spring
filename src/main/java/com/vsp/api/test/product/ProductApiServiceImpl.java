package com.vsp.api.test.product;

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
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.vsp.api.productbusupdate.base.StringConverter;


import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;

@Service("ProductApiService")
public class ProductApiServiceImpl implements ProductApiService, StringConverter {

    private static final Logger logger = LoggerFactory.getLogger(ProductApiServiceImpl.class);

    private static final String AUTHORIZATION_TYPE_BEARER = "bearer";
    private static final String CLIENT_CREDENTIALS = "client_credentials";

    private static final String OAUTH_CLIENT_SCOPES = "oauth.client_scopes";
    private static final String OAUTH_CLIENT_ID = "oauth.client_id";
    private static final String OAUTH_CLIENT_SECRET = "oauth.client_secret";
    private static final String OAUTH_URI = "oauth.resource_uri";

    private static final String PRODUCT_RESOURCE_URI = "product.resource_uri";

    private static final String PRESERVE_SUSPENDS_URI_SUFFIX = "/preserveSuspends";

    @Autowired
    @Qualifier("productUpdateRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private Environment environment;

    public String getToken() {
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

    @Override
    public ClientProducts searchClientProducts(String clientId, String classId, String asOfDate, boolean active) {
        ClientProducts cps = null;

        try {
            asOfDate = convertStringDate(asOfDate, InputFormat.DEFAULT, InputFormat.JSON);
            String searchURL = getSearchByClientClassAsofURL(clientId, classId, asOfDate, active);
            HttpEntity<?> httpRequest = buildGETHttpRequest();
            ResponseEntity<ClientProducts> response = restTemplate.exchange(searchURL, HttpMethod.GET, httpRequest,
                    ClientProducts.class);
            cps = response.getBody();
        } catch (Exception e) {
            logger.error("ProductApiService : Error searching client products - " + e.getMessage(), e);
        }

        return cps;
    }

    @Override
    public ClientProducts searchClientProducts(String clientId, String classId, boolean active) {
        ClientProducts cps = searchClientProducts(clientId, classId, null, active);
        return cps;

    }

    private HttpEntity<?> buildGETHttpRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", AUTHORIZATION_TYPE_BEARER + " " + getToken());
        return new HttpEntity<>(headers);
    }

    public String getResourceURL() {
        String resourceURL = "";
        try {
            resourceURL = environment.getProperty(PRODUCT_RESOURCE_URI);
        } catch (Exception e) {
            logger.error("ProductApiService : exception in getResourceURL()", e);
        }
        return resourceURL;
    }

    private String getSearchByClientClassAsofURL(String clientId, String classId, String asOfDate, boolean active) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getResourceURL())
                .queryParam("clientId", clientId).queryParam("classId", classId).queryParam("active", active)
                .queryParam("sort", "effectivedate");

        if (asOfDate != null) {
            builder.queryParam("asofdate", asOfDate);
        }

        return builder.toUriString();
    }

    public ClientProduct retrieveClientProductByClientProductId(String clientProductId) {
        ClientProduct cp = null;
        String retrieveURL = getRetrieveByIdURL(clientProductId);
        HttpEntity<?> httpRequest = buildGETHttpRequest();
        ResponseEntity<ClientProduct> response = restTemplate.exchange(retrieveURL, HttpMethod.GET, httpRequest,
                ClientProduct.class);

        cp = response.getBody();

        return cp;
    }

    private String getRetrieveByIdURL(String clientProductId) {
        return getResourceURL() + "/" + clientProductId;
    }

    public ClientProduct retrieveClientProductByDivisionClass(String clientId, String divisionId, String classId,
                                                              String asOfDate) {
        ClientProduct cp = null;
        String retrieveURL = getRetrieveByClientDivClassURL(clientId, divisionId, classId, asOfDate);
        HttpEntity<?> httpRequest = buildGETHttpRequest();
        ResponseEntity<ClientProduct> response = restTemplate.exchange(retrieveURL, HttpMethod.GET, httpRequest,
                ClientProduct.class);

        cp = response.getBody();

        return cp;
    }

    public ClientProduct retrieveClientProductByCPK(String clientId, String memberListId, String marketTierId,
                                                    String marketNetworkId, String marketProductId, String asOfDate, boolean active) {
        ClientProduct cp = null;
        String retrieveURL = getRetrieveByCpkURL(clientId, memberListId, marketTierId, marketNetworkId, marketProductId,
                asOfDate, active);
        HttpEntity<?> httpRequest = buildGETHttpRequest();
        ResponseEntity<ClientProduct> response = restTemplate.exchange(retrieveURL, HttpMethod.GET, httpRequest,
                ClientProduct.class);

        cp = response.getBody();

        return cp;
    }

    private String getRetrieveByCpkURL(String clientId, String memberListId, String marketTierId,
                                       String marketNetworkId, String marketProductId, String asOfDate, boolean active) {
        String retrieveURL = getResourceURL() + "/" + clientId + "-" + memberListId + "-" + marketTierId + "-"
                + marketNetworkId + "-" + marketProductId;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(retrieveURL).queryParam("asofdate", asOfDate)
                .queryParam("active", active);
        return builder.toUriString();
    }

    private String getRetrieveByClientDivClassURL(String clientId, String divisionId, String classId, String asOfDate) {
        String retrieveURL = getResourceURL() + "/" + clientId + "-" + divisionId + "-" + classId;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(retrieveURL).queryParam("asofdate", asOfDate);
        return builder.toUriString();
    }

    public ClientProducts createClientProduct(ClientProducts clientProducts) {
        return createClientProduct(clientProducts, true);
    }

    public ClientProducts createClientProduct(ClientProducts clientProducts, boolean newVersion) {
        ClientProducts cps = null;
        String createURL = null;

        if (newVersion) {
            for (ClientProduct cp : clientProducts) {
                cp.setActive(false);
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", AUTHORIZATION_TYPE_BEARER + " " + getToken());
        HttpEntity<?> httpRequest = new HttpEntity<>(clientProducts, headers);

        createURL = getResourceURL() + PRESERVE_SUSPENDS_URI_SUFFIX;

        ResponseEntity<ClientProducts> response = restTemplate.exchange(createURL, HttpMethod.POST, httpRequest,
                ClientProducts.class);

        cps = response.getBody();

        return cps;
    }

    @Override
    public ClientProduct activateClientProduct(ClientProduct clientProduct, String userId) {
        ClientProductKey cpk = clientProduct.getClientProductKey();
        String clientId = cpk.getClientId();
        String memberListId = cpk.getMemberListId();
        String marketTierId = cpk.getPrimaryMarketProduct().getMarketProductTier();
        String marketNetworkId = cpk.getPrimaryMarketProduct().getNetwork();
        String marketProductId = cpk.getPrimaryMarketProduct().getIdentifier();
        String effectiveStartDate = clientProduct.getEffectivePeriod().getBegin().toString("yyyy-MM-dd");
        String approveURL = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.valueOf("application/vnd.vsp.clientproduct"));
        headers.add("Authorization", AUTHORIZATION_TYPE_BEARER + " " + getToken());
        HttpEntity<?> httpRequest = new HttpEntity<>(headers);

        approveURL = getResourceURL() + "/" + clientId + "-" + memberListId + "-" + marketTierId + "-" + marketNetworkId
                + "-" + marketProductId + "-" + effectiveStartDate + "-" + userId + PRESERVE_SUSPENDS_URI_SUFFIX;
        ResponseEntity<ClientProduct> response = restTemplate.exchange(approveURL, HttpMethod.POST, httpRequest,
                ClientProduct.class);
        clientProduct = response.getBody();

        return clientProduct;
    }

    private RuntimeException createRuntimeException(Exception e, String url, String baseMessage) {
        String message = null;

        if (StringUtils.isBlank(url)) {
            message = MessageFormat.format("{0}, Exception: {1}", baseMessage, e.getMessage());
        } else {
            message = MessageFormat.format("{0}, URL: {1}, Exception: {2}", baseMessage, url, e.getMessage());
        }

        return new RuntimeException(message, e);
    }

}
