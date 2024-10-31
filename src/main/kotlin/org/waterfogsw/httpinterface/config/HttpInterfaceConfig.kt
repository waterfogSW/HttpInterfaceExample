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

    @Bean
    fun userClient(restClientBuilder: RestClient.Builder): UserClient {
        // RestClient 생성 및 기본 설정
        val restClient = restClientBuilder
            .baseUrl(baseUrl)
            .build()

        // RestClient를 HTTP Interface에 연결
        val adapter = RestClientAdapter.create(restClient)
        val factory = HttpServiceProxyFactory.builderFor(adapter)
            .build()

        return factory.createClient(UserClient::class.java)
    }
}
