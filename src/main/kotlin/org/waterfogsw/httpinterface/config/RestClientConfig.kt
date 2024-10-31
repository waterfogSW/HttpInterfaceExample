package org.waterfogsw.httpinterface.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.retry.backoff.ExponentialBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException

@Configuration
class RestClientConfig(
    @Value("\${rest.client.base-url}")
    private val baseUrl: String
) {

    companion object {

        private val logger = LoggerFactory.getLogger(RestClientConfig::class.java)
    }

    @Bean
    fun defaultRestClientBuilder(): RestClient.Builder {
        return RestClient
            .builder()
            .baseUrl(baseUrl)
            .defaultHeaders { headers ->
                headers.setBearerAuth(generateToken())
            }
            .requestInterceptor { request, body, execution ->
                logger.debug("Request: {} {}", request.method, request.uri)
                execution.execute(request, body)
            }
            .requestInterceptor(LoggingInterceptor())
            .defaultStatusHandler(HttpStatusCode::isError) { _, response ->
                when (response.statusCode) {
                    HttpStatus.NOT_FOUND -> throw RestClientException("Resource not found")
                    HttpStatus.UNAUTHORIZED -> throw RestClientException("Unauthorized")
                    HttpStatus.BAD_REQUEST -> throw RestClientException("Invalid request")
                    else -> throw RestClientException("HTTP error: ${response.statusCode}")
                }
            }
    }

    // 상세 로깅을 위한 인터셉터 구현
    class LoggingInterceptor : ClientHttpRequestInterceptor {

        override fun intercept(
            request: HttpRequest,
            body: ByteArray,
            execution: ClientHttpRequestExecution
        ): ClientHttpResponse {
            logger.info("=== Request ===")
            logger.info("URI: {}", request.uri)
            logger.info("Method: {}", request.method)
            logger.info("Headers: {}", request.headers)

            val response = execution.execute(request, body)

            logger.info("=== Response ===")
            logger.info("Status: {}", response.statusCode)

            return response
        }

        companion object {

            private val logger = LoggerFactory.getLogger(LoggingInterceptor::class.java)
        }
    }

    private fun generateToken(): String {
        return "token"
    }

}
