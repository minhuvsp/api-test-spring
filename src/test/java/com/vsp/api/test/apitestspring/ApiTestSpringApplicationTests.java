package com.vsp.api.test.apitestspring;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.vsp.api.network.model.ProviderNetwork;
import com.vsp.api.network.model.ProviderNetworks;
import com.vsp.api.network.service.ProviderNetworkService;
import com.vsp.api.product.model.ClientProduct;
import com.vsp.api.product.service.ProductApiService;

@RunWith(SpringRunner.class)
//@SpringBootTest(classes = { ApiTestSpringApplication.class})
//@SpringBootTest
@SpringBootTest(classes = {ApiTestSpringApplication.class})
@ActiveProfiles("embedded")
public class ApiTestSpringApplicationTests {

    @Autowired
    @Qualifier("productApiService")
    private ProductApiService productApiService;

    @Autowired
    @Qualifier("providerNetworkService")
    private ProviderNetworkService providerNetworkService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testRetrieveProviderNetwork() {

        ProviderNetwork network = providerNetworkService.getProviderNetwork("AFPR"); // default Accept version=5.0

        if (network != null) {
            System.out.println("retrieve result: Name=" + network.getName() + " NetworkType=" + network.getNetworkType());
        }

        network = providerNetworkService.getProviderNetwork("4.9", "AFPR");

        if (network != null) {
            System.out.println("retrieve result: Name=" + network.getName() + " NetworkType=" + network.getNetworkType());
        }

    }

    @Test
    public void testSearchProviderNetworks() {

        ProviderNetworks networks = providerNetworkService.getProviderNetworks();

        if (networks != null) {
            System.out.println("search result empty? " + networks.isEmpty());

            int count = 1;
            for (ProviderNetwork cp : networks) {
                System.out.println("search result = " + count++);
            }
        }

    }

    @Test
    public void testSearchClientProducts() {
        String clientId = "12002203";
        String classId = "0001";
        String asOfDate = "2018-01-01";

        List<ClientProduct> cps = productApiService.searchClientProducts(clientId, classId, asOfDate, true);

        if (cps != null) {
            System.out.println("search result empty? " + cps.isEmpty());

            int count = 1;
            for (ClientProduct cp : cps) {
                System.out.println("search result = " + count++);
            }
        }

    }

    @Test
    public void testRetrieveClientProductByDivisionClass() {
        String clientId = "30084834";
        String classId = "0006";
        String divisionId = "0006";
        String asOfDate = "2019-04-01";

        ClientProduct cp = productApiService.retrieveClientProductByDivisionClass(clientId, divisionId, classId,
                asOfDate);
        System.out.println("Retrieve client product:" + ((cp == null) ? "failed" : "success"));
    }

}
