package com.example.springelasticsearch.repository

import com.example.springelasticsearch.entity.User
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface UserSearchRepository : ElasticsearchRepository<User,Long> , CustomUserSearchRepository {

    fun findByBasicProfile_NameContains(name: String) : List<User>
}