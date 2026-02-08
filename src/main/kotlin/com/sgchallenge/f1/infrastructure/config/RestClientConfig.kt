package com.sgchallenge.f1.infrastructure.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class RestClientConfig {

    @Bean
    fun openF1RestClient(@Value("\${openf1.base-url}") baseUrl: String): RestClient =
        RestClient.builder().baseUrl(baseUrl).build()
}
