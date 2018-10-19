package com.vsp.api.productbusupdate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
* These values are automatically populated from configuration YAML files or Consul.
*
* Autowire this in your class to use it.
*
* See https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config
*/
@Component
@ConfigurationProperties(prefix = "product-bus-update")
public class AppConfig {

    private String someConfigValue;
    private Map<String, Feature> features;
    private List<String> items;

    public String getSomeConfigValue() {
        return someConfigValue;
    }

    public void setSomeConfigValue(String someConfigValue) {
        this.someConfigValue = someConfigValue;
    }

    public Map<String, Feature> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Feature> features) {
        this.features = features;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public static class Feature {

        private boolean enabled;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

    }

}