package com.example.springelasticsearchv2.repository.elastic

import com.example.springelasticsearchv2.entity.enum.PostStatus
import com.example.springelasticsearchv2.model.elastic.PostDoc
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.data.elasticsearch.core.query.Query

class CustomPostSearchRepositoryImpl(
    private val elasticsearchOperations: ElasticsearchOperations
): CustomPostSearchRepository {
    override fun searchAllByTest(text: String, pageable: Pageable): Page<PostDoc> {
        val criteria =
            Criteria("postStatus").`is`(PostStatus.PUBLISH)
                .subCriteria(
                    Criteria("title").matches(text)
                        .or("content").matches(text)
                        .or("categoryName").matches(text)
                        .or("tagName").matches(text)
                )
        val query = CriteriaQuery(criteria).setPageable<Query>(pageable)
        val search = elasticsearchOperations.search(query, PostDoc::class.java)
        val list: List<PostDoc> = search.searchHits.map { it.content }
        return PageImpl(list, pageable, list.size.toLong())
    }
}