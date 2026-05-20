package com.project.socialX.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("SocialX REST API Documentation")
                        .version("1.0.0")
                        .description("Đây là tài liệu API cho ứng dụng mạng xã hội SocialX. Cung cấp các RESTful APIs cho ứng dụng Client (ReactJS/Mobile).")
                        .contact(new Contact()
                                .name("SocialX Team")
                                .email("contact@socialx.com")
                                .url("https://socialx.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description("Vui lòng nhập JWT Token được cấp sau khi đăng nhập thành công vào đây. VD: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                                )
                );
    }
}
