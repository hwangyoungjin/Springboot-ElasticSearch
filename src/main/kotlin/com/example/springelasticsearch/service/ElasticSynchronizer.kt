package com.example.springelasticsearch.service

import com.example.springelasticsearch.entity.User
import com.example.springelasticsearch.repository.UserRepository
import com.example.springelasticsearch.repository.UserSearchRepository
import mu.KotlinLogging
import org.springframework.data.jpa.domain.Specification
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Date
import java.time.LocalDateTime
import javax.persistence.criteria.*


private val logger = KotlinLogging.logger { }

@Service
class ElasticSynchronizer(
    private val userSearchRepository: UserSearchRepository,
    private val userRepository: UserRepository,
) {

    @Scheduled(cron = "*/10 * * * * *") //10초 마다 실행
    @Transactional
    fun sync(){
        logger.info("Start Syncing - {}", LocalDateTime.now())
        syncUsers()
        logger.info("end Syncing - {}", LocalDateTime.now())
    }

    private fun syncUsers(){
        val startDate = LocalDateTime.now().minusHours(2)
        val endDate = LocalDateTime.now()

        val userList: List<User> = if (userSearchRepository.count() == 0L) {
            userRepository.findAll()
        } else {
            userRepository.findAllByModificationDate(startDate,endDate)
        }
        for (user in userList) {
            logger.info("Syncing User - {}", user.id)
            userSearchRepository.save(user)
        }
    }
}