package com.vsp.api.productbusupdate.config;

import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTypeResolverBuilder;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.vsp.api.product.service.ProductApiService;
import com.vsp.api.product.service.ProductApiServiceImpl;
//import com.vspglobal.cloud.jackson.DateMidnightModule;

@Configuration
@EnableWebMvc
public class CommonConfig extends WebMvcConfigurerAdapter {
//public class CommonConfig {
    /**
     * Logger instance.
     */
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CommonConfig.class);

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowCredentials(true)
            .allowedOrigins("*")
            .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH")
            .allowedHeaders("*")
            .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Methods",
                "Access-Control-Allow-Headers", "Access-Control-Max-Age",
                "Access-Control-Request-Headers", "Access-Control-Request-Method");
    }

    @Bean(name = "productApiService")
    public ProductApiService productApiService() {
    	ProductApiService productApiService = new ProductApiServiceImpl();
        return productApiService;
    }

    @Bean(name = "productUpdateRestTemplate")
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        RestTemplate template = builder.build();
        template.getMessageConverters().add(0, mappingJacksonHttpMessageConverter());
        return template;
    }

    private MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter() {
        return new MappingJackson2HttpMessageConverter(jsonMapper());
    }

    public static ObjectMapper jsonMapper() {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new JodaModule());
//        jsonMapper.registerModule(new DateMidnightModule());
        jsonMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        jsonMapper.setSerializationInclusion(Include.NON_EMPTY);
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jsonMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);

        TypeResolverBuilder<?> typer = new DefaultTypeResolverBuilder(
            DefaultTyping.OBJECT_AND_NON_CONCRETE) {
            private static final long serialVersionUID = 1L;

            public boolean useForType(JavaType t) {
                boolean applies = super.useForType(t);
                if (applies) {
                    // make sure type is in package com.vsp
                    if (t.getRawClass().getPackage().getName()
                        .startsWith("com.vsp"))
                        return true;
                }

                return false;
            }
        };
        // we'll always use full class name, when using defaulting
        typer = typer.init(JsonTypeInfo.Id.CLASS, null);
        typer = typer.inclusion(JsonTypeInfo.As.PROPERTY);
        typer = typer.typeProperty("_class");
        jsonMapper.setDefaultTyping(typer);
        return jsonMapper;
    }

}
