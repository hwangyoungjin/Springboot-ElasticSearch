package com.example.springelasticsearchv2.repository.elastic

import com.example.springelasticsearchv2.model.elastic.PostDoc
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CustomPostSearchRepository {
    fun searchAllByTest(text: String, pageable: Pageable): Page<PostDoc>
}