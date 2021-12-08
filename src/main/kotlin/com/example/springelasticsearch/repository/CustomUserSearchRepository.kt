package com.example.springelasticsearch.repository

import com.example.springelasticsearch.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CustomUserSearchRepository {
    fun searchByName(name : String, pageable: Pageable) : List<User>
}