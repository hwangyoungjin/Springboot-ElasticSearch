package com.example.springelasticsearchv2.repository

import com.example.springelasticsearchv2.entity.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository: JpaRepository<Category,Long> {
}