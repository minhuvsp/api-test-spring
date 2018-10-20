package com.vsp.api.test.apitestspring;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.vsp.api.product.model.ClientProduct;
import com.vsp.api.product.service.ProductApiService;

@RunWith(SpringRunner.class)
//@SpringBootTest(classes = { ApiTestSpringApplication.class})
//@SpringBootTest
@SpringBootTest(classes = { ApiTestSpringApplication.class})
@ActiveProfiles("embedded")
public class ApiTestSpringApplicationTests {

    @Autowired
	@Qualifier("productApiService")
    private ProductApiService productApiService;

    @Test
	public void contextLoads() {
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

}
