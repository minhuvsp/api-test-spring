package com.vsp.api.network.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.vsp.api.network.model.ProviderNetwork;
import com.vsp.api.network.model.ProviderNetworks;
import com.vsp.api.service.BaseService;

@Service("ProviderNetworkService")
public class ProviderNetworkServiceImpl extends BaseService implements ProviderNetworkService {

    private static final Logger logger = LoggerFactory.getLogger(ProviderNetworkServiceImpl.class);

    private static final String PROVIDER_NETWORK_RESOURCE_URI = "network.resource_uri";
    private static final String VERSION = "5.0";

    private String getRetrieveURL(String networkId) {
        String retrieveURL = getResourceURL(PROVIDER_NETWORK_RESOURCE_URI) + "/" + networkId;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(retrieveURL);
        return builder.toUriString();
    }

    @Override
    public ProviderNetworks getProviderNetworks() {
        ProviderNetworks networks = null;
        try {
            String searchURL = getResourceURL(PROVIDER_NETWORK_RESOURCE_URI);
            HttpEntity<?> httpRequest = buildGETHttpRequestWithAcceptVersion(VERSION);
            ResponseEntity<ProviderNetworks> response = restTemplate.exchange(searchURL, HttpMethod.GET, httpRequest,
                    ProviderNetworks.class);
            networks = response.getBody();
        } catch (Exception e) {
            logger.error("ProviderNetworkServiceImpl : Error searching provider network - " + e.getMessage(), e);
        }

        return networks;
    }

    @Override
    public ProviderNetwork getProviderNetwork(String networkId) {
        ProviderNetwork result = null;
        try {
            String retrieveURL = getRetrieveURL(networkId);
            HttpEntity<?> httpRequest = buildGETHttpRequestWithAcceptVersion(VERSION);
            ResponseEntity<ProviderNetwork> response = restTemplate.exchange(retrieveURL, HttpMethod.GET, httpRequest,
                    ProviderNetwork.class);
            result = response.getBody();
        } catch (Exception e) {
            logger.error("ProviderNetworkServiceImpl : Error retrieve provider network - " + e.getMessage(), e);
        }

        return result;
    }

    @Override
    public ProviderNetwork getProviderNetwork(String version, String networkId) {
        ProviderNetwork result = null;
        try {
            String retrieveURL = getRetrieveURL(networkId);
            HttpEntity<?> httpRequest = buildGETHttpRequestWithAcceptVersion(version);
            ResponseEntity<ProviderNetwork> response = restTemplate.exchange(retrieveURL, HttpMethod.GET, httpRequest,
                    ProviderNetwork.class);
            result = response.getBody();
        } catch (Exception e) {
            logger.error("ProviderNetworkServiceImpl : Error retrieve provider network - " + e.getMessage(), e);
        }

        return result;
    }

}
