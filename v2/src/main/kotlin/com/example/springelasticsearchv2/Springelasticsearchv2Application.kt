package com.example.springelasticsearchv2

import com.example.springelasticsearchv2.repository.elastic.PostSearchRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaAuditing
@SpringBootApplication
@EnableJpaRepositories(
    excludeFilters = [ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = arrayOf(PostSearchRepository::class)
    )]
)
class Springelasticsearchv2Application

fun main(args: Array<String>) {
    runApplication<Springelasticsearchv2Application>(*args)
}
