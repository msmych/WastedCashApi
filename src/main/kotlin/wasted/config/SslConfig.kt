package wasted.config

import org.apache.catalina.connector.Connector
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("prod")
class SslConfig {

  @Bean
  fun httpConnector(): Connector {
    val connector = Connector("org.apache.coyote.http11.Http11NioProtocol")
    connector.scheme = "http"
    connector.port = 80
    connector.secure = false
    connector.redirectPort = 443
    return connector
  }
}
