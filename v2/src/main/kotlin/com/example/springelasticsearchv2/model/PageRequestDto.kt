package com.example.springelasticsearchv2.model

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.time.LocalDateTime

class PageRequestDto(

    var startDate: LocalDateTime? = null,

    var endDate: LocalDateTime? = null,

    var page: Int = 0,

    var size: Int = 20,

    var sort: String = "DESC",

    var sortDir: String = "createdAt"
) {
    fun pageRequest(): PageRequest {
        return PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(sort), sortDir))
    }
}