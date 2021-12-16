package com.example.springelasticsearch.repository

import com.example.springelasticsearch.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface UserRepository : JpaRepository<User,Long>, JpaSpecificationExecutor<User> {

    /**
     * 최근 수정된 User 조회 (2시간 간격)
     */
    @Query(" select u from User u " +
            "where u.modificationDate >=:startDate " +
            "and u.modificationDate <=:endDate ")
    fun findAllByModificationDate(@Param("startDate") startDateTime: LocalDateTime, @Param("endDate") endDate:LocalDateTime) : List<User>
}