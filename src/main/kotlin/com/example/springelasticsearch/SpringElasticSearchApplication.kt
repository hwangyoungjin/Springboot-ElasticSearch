package com.example.springelasticsearch

import com.example.springelasticsearch.repository.UserSearchRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableJpaRepositories(excludeFilters = [ComponentScan.Filter(
	type = FilterType.ASSIGNABLE_TYPE,
	classes = arrayOf(UserSearchRepository::class)
)])
class SpringElasticSearchApplication

fun main(args: Array<String>) {
	runApplication<SpringElasticSearchApplication>(*args)
}
