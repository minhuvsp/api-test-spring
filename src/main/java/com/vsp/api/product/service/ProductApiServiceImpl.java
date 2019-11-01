package com.vsp.api.product.service;

import java.text.MessageFormat;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.vsp.api.product.model.ClientProduct;
import com.vsp.api.product.model.ClientProductKey;
import com.vsp.api.product.model.ClientProducts;
import com.vsp.api.productbusupdate.base.InputFormat;
import com.vsp.api.productbusupdate.base.StringConverter;
import com.vsp.api.service.BaseService;

@Service("ProductApiService")
public class ProductApiServiceImpl extends BaseService implements ProductApiService, StringConverter {

	private static final Logger logger = LoggerFactory.getLogger(ProductApiServiceImpl.class);

	private static final String PRODUCT_RESOURCE_URI = "product.resource_uri";

	private static final String PRESERVE_SUSPENDS_URI_SUFFIX = "/preserveSuspends";

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

	private String getSearchByClientClassAsofURL(String clientId, String classId, String asOfDate, boolean active) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getResourceURL(PRODUCT_RESOURCE_URI))
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
		return getResourceURL(PRODUCT_RESOURCE_URI) + "/" + clientProductId;
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
		String retrieveURL = getResourceURL(PRODUCT_RESOURCE_URI) + "/" + clientId + "-" + memberListId + "-" + marketTierId + "-"
				+ marketNetworkId + "-" + marketProductId;
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(retrieveURL).queryParam("asofdate", asOfDate)
				.queryParam("active", active);
		return builder.toUriString();
	}

	private String getRetrieveByClientDivClassURL(String clientId, String divisionId, String classId, String asOfDate) {
		String retrieveURL = getResourceURL(PRODUCT_RESOURCE_URI) + "/" + clientId + "-" + divisionId + "-" + classId;
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

		createURL = getResourceURL(PRODUCT_RESOURCE_URI) + PRESERVE_SUSPENDS_URI_SUFFIX;

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

		approveURL = getResourceURL(PRODUCT_RESOURCE_URI) + "/" + clientId + "-" + memberListId + "-" + marketTierId + "-" + marketNetworkId
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
