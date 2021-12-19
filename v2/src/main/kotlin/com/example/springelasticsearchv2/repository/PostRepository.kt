package com.example.springelasticsearchv2.repository

import com.example.springelasticsearchv2.entity.Post
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<Post, Long> {
}