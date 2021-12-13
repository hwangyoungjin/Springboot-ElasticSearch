package com.example.springelasticsearch.service

import com.example.springelasticsearch.dto.UserRequestDto
import com.example.springelasticsearch.dto.UserResponseDto
import com.example.springelasticsearch.entity.User
import com.example.springelasticsearch.repository.UserRepository
import com.example.springelasticsearch.repository.UserSearchRepository
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Transactional(readOnly = true)
@Service
class UserService(
    private val userRepository: UserRepository,
    private val userSearchRepository: UserSearchRepository
) {
    @Transactional
    fun save(userRequestDto: UserRequestDto): Long {
        val user = userRequestDto.toEntity()
        val savedUser: User = userRepository.save(user)
        userSearchRepository.save<User>(user)
        return savedUser.id!!
    }

    fun searchByName(name: String, pageable: Pageable): List<UserResponseDto> {
        // userSearchRepository.findByBasicProfile_NameContains(name) 가능
        return userSearchRepository.searchByName(name, pageable)
            .map { of(it) }
    }

    private fun of(user: User):UserResponseDto{
        return UserResponseDto(
            name = user.basicProfile!!.name,
            description = user.basicProfile!!.description,
            modificationDate = user.modificationDate
        )
    }
}