package com.example.springelasticsearchv2.repository

import com.example.springelasticsearchv2.entity.Tag
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository : JpaRepository<Tag, Long> {
}