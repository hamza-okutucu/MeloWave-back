package melowave.config;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@EnableWebMvc
public class SwaggerConfig implements WebMvcConfigurer {
    
    @Value("${api.base-path}")
    private String apiBasePath;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("melowave"))
                .paths(PathSelectors.regex("/.*"))
                .build().apiInfo(apiInfoMetaData())
                .pathMapping(apiBasePath);
    }

    private ApiInfo apiInfoMetaData() {
        return new ApiInfoBuilder().title("melowave")
                .description("Music streaming RESTful API")
                .contact(new Contact("Hamza Okutucu", "https://hamza-okutucu.github.io/Portfolio/", "hamza.okutucu@outlook.com"))
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .version("1.0.0")
                .build();
    }
}
