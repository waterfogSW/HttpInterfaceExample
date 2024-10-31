package org.waterfogsw.httpinterface.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import org.waterfogsw.httpinterface.client.UserClient

@Configuration
class HttpInterfaceConfig(
    @Value("\${rest.client.base-url}")
    private val baseUrl: String
) {


}
