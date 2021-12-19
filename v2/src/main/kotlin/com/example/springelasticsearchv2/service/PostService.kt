package com.example.springelasticsearchv2.service

import com.example.springelasticsearchv2.entity.Category
import com.example.springelasticsearchv2.entity.Post
import com.example.springelasticsearchv2.entity.Tag
import com.example.springelasticsearchv2.model.PostDTO
import com.example.springelasticsearchv2.model.elastic.PostDoc
import com.example.springelasticsearchv2.repository.CategoryRepository
import com.example.springelasticsearchv2.repository.PostRepository
import com.example.springelasticsearchv2.repository.elastic.PostSearchRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class PostService(
    private val postRepository: PostRepository,
    private val postSearchRepository: PostSearchRepository,
    private val categoryRepository: CategoryRepository
) {
    /**
     * Update, delete 도 create와 같은 방식
     */
    fun createPost(dto: PostDTO) : Long{
        val post = dto.toEntity()
        dto.tags.forEach { post.addTag(Tag(name = it)) }
        //카테고리는 저장되어 있다고 가정
        val savedCategory = categoryRepository.save(Category(name = dto.category?:""))
        post.updateCategory(savedCategory)

        val savePost = postRepository.save(post)
        postSearchRepository.save(docOf(savePost))

        return savePost.id
    }

    private fun docOf(post: Post): PostDoc {
        return PostDoc(
            postId = post.id,
            title = post.title,
            content = post.content,
            postStatus = post.status,
            categoryName = post.category?.name,
            tagName = post.tags.map { it.name }.toList().joinToString(","),
            createdAt = post.createdAt.toString()
        )
    }


    /**
     * search postDoc
     */
    fun findAllByText(text: String, pageable: Pageable): Page<PostDTO> {
        val postDocs = postSearchRepository.searchAllByTest(text, pageable)
        return postDocs.map { findById(it.postId)}
    }

    fun findById(id:Long): PostDTO{
        val post = postRepository.findById(id).orElseThrow {
            RuntimeException("NotFound Post")
        }
        return dtoOf(post)
    }

    private fun dtoOf(post: Post):PostDTO{
        return PostDTO(
            title = post.title,
            content = post.content,
            category = post.category!!.name,
            tags = post.tags.map { tag -> tag.name }.toMutableList()
        )
    }
}