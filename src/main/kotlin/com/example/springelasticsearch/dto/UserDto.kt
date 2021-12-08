package com.example.springelasticsearch.dto

import com.example.springelasticsearch.entity.BasicProfile
import com.example.springelasticsearch.entity.User


data class UserRequestDto(
    var id: Long? = null,
    val name : String = "",
    val description : String = ""
){
    fun toEntity():User{
        return User(
            basicProfile = BasicProfile(
                name = this.name,
                description = this.description
            )

        )
    }
}

data class UserResponseDto(
    var id: Long? = null,
    val name : String = "",
    val description : String = ""
)
