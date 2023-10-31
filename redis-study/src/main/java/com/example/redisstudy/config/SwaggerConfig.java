package com.example.redisstudy.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {
    @Value("${spring.swagger2.enabled}")
    private Boolean enabled;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("测试 title")
                        .description("SpringBoot3 集成 Swagger3")
                        .version("v1"))
                .externalDocs(new ExternalDocumentation()
                        .description("项目API文档")
                        .url("/"));
    }
}

