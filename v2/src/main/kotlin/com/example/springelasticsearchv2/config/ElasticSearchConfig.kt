package com.example.springelasticsearchv2.config

import com.example.springelasticsearchv2.repository.elastic.PostSearchRepository
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.RestClients
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories

@Configuration
@EnableElasticsearchRepositories(basePackageClasses = [PostSearchRepository::class])
class ElasticSearchConfig(
    @Value("\${elasticsearch.url}")
    private val elasticSearchUrl : String
) : AbstractElasticsearchConfiguration() {

    override fun elasticsearchClient(): RestHighLevelClient {
        val clientConfiguration = ClientConfiguration.builder()
            /**elastic server endpoint**/
            .connectedTo(elasticSearchUrl)
            .build()
        return RestClients.create(clientConfiguration).rest()
    }
}
