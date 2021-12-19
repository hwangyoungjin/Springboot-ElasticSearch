package com.example.springelasticsearchv2.repository.elastic

import com.example.springelasticsearchv2.model.elastic.PostDoc
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface PostSearchRepository : ElasticsearchRepository<PostDoc,Long>, CustomPostSearchRepository {
}