package com.vsp.api.product.service;

import com.vsp.api.product.model.ClientProduct;
import com.vsp.api.product.model.ClientProducts;

public interface ProductApiService {

	public ClientProducts searchClientProducts(String clientId, String classId, String asOfDate, boolean active);

	public ClientProducts searchClientProducts(String clientId, String classId, boolean active);

	public ClientProduct retrieveClientProductByClientProductId(String clientProductId);

	public ClientProduct retrieveClientProductByDivisionClass(String clientId, String divisionId, String classId,
			String asOfDate);

	public ClientProduct retrieveClientProductByCPK(String clientId, String memberListId, String marketTierId,
			String marketNetworkId, String marketProductId, String asofdate, boolean active);

	public ClientProducts createClientProduct(ClientProducts clientProducts);

	public ClientProducts createClientProduct(ClientProducts clientProducts, boolean newVersion);

	public ClientProduct activateClientProduct(ClientProduct clientProduct, String userId);
}
