package lookids.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    private static final String BEARER_TOKEN_PREFIX = "Bearer";

    @Bean
    public OpenAPI openAPI() {

        String securityJwtName = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securityJwtName);
        Components components = new Components().addSecuritySchemes(securityJwtName,
            new SecurityScheme().name(securityJwtName)
                .type(SecurityScheme.Type.HTTP)
                .scheme(BEARER_TOKEN_PREFIX)
                .bearerFormat(securityJwtName));

        return new OpenAPI().addSecurityItem(securityRequirement)
            .components(components)
            .addServersItem(new Server().url("/feed read-service"))
            .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info().title("FEED READ SERVICE 문서").description("lookids API 테스트를 위한 Swagger UI").version("1.0.0");
    }

}