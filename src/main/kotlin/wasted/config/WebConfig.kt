package wasted.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import wasted.token.TokenInterceptor

@Configuration
class WebConfig : WebMvcConfigurer {

    @Autowired
    lateinit var tokenInterceptor: TokenInterceptor

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(tokenInterceptor)
    }
}