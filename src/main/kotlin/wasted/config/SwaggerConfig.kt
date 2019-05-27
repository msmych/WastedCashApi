package wasted.config

import com.google.common.base.Predicates.not
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.builders.PathSelectors.regex
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.schema.ModelRef
import springfox.documentation.spi.DocumentationType.SWAGGER_2
import springfox.documentation.spring.web.plugins.Docket

@Configuration
class SwaggerConfig {

    @Bean
    fun getDocket(): Docket {
        return Docket(SWAGGER_2)
                .globalOperationParameters(
                        listOf(ParameterBuilder()
                                .name("api-token")
                                .description("API token")
                                .modelRef(ModelRef("string"))
                                .parameterType("header")
                                .required(true)
                                .build()))
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(not(regex("/error.*")))
                .build()
    }
}