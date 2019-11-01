package com.vsp.api.network.service;

import com.vsp.api.network.model.ProviderNetwork;
import com.vsp.api.network.model.ProviderNetworks;

public interface ProviderNetworkService {

    public ProviderNetworks getProviderNetworks();

    public ProviderNetwork getProviderNetwork(String networkId);
    public ProviderNetwork getProviderNetwork(String version, String networkId);

}
