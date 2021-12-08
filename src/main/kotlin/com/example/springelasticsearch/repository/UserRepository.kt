package com.example.springelasticsearch.repository

import com.example.springelasticsearch.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User,Long> {
}