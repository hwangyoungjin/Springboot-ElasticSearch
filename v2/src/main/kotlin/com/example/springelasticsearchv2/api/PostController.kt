package com.example.springelasticsearchv2.api

import com.example.springelasticsearchv2.model.PageRequestDto
import com.example.springelasticsearchv2.model.PostDTO
import com.example.springelasticsearchv2.service.PostService
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.awt.print.Pageable

@RestController
@RequestMapping("/api/posts")
class PostController(
    private val postService: PostService
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun createPost(
        @RequestBody dto: PostDTO
    ): Long {
        return postService.createPost(dto)
    }

    @GetMapping
    fun getPosts(
        pageRequestDto: PageRequestDto,
        @RequestParam(name = "text") text: String,
    ): Page<PostDTO> {
        return postService.findAllByText(text, pageRequestDto.pageRequest())
    }
}