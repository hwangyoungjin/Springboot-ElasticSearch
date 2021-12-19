package com.example.springelasticsearchv2.model

import com.example.springelasticsearchv2.entity.Post
import com.example.springelasticsearchv2.entity.enum.PostStatus

data class PostDTO(
    val title:String = "",
    val content:String = "",
    val status:PostStatus = PostStatus.DRAFT,
    val tags: MutableList<String> = mutableListOf(),
    val category:String? = null
){
    fun toEntity(): Post{
        return Post(
            title = title,
            content = content,
            status = status
        )
    }
}