package com.example.springelasticsearch.repository

import com.example.springelasticsearch.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.data.elasticsearch.core.query.Query

class CustomUserSearchRepositoryImpl(
    private val elasticsearchOperations: ElasticsearchOperations
) : CustomUserSearchRepository {
    override fun searchByName(name: String, pageable: Pageable): List<User> {
        val criteria: Criteria = Criteria.where("basicProfile.name").contains(name)
        val query: Query = CriteriaQuery(criteria).setPageable(pageable)
        val search = elasticsearchOperations.search(query, User::class.java)
        return search.searchHits.map { it.content }
    }

}