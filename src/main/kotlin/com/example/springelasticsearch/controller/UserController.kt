package com.example.springelasticsearch.controller

import com.example.springelasticsearch.dto.UserRequestDto
import com.example.springelasticsearch.dto.UserResponseDto
import com.example.springelasticsearch.service.UserService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI


@RequestMapping("/api")
@RestController
class UserController(
    private val userService: UserService
){
    @PostMapping("/users")
    fun save(@RequestBody userRequest: UserRequestDto): ResponseEntity<Void> {
        val id: Long = userService.save(userRequest)
        val uri: URI = URI.create(id.toString())
        return ResponseEntity.created(uri)
            .build()
    }

    @GetMapping("/users/{name}")
    fun search(@PathVariable name: String, pageable: Pageable): ResponseEntity<List<UserResponseDto>> {
        val userResponses: List<UserResponseDto> = userService.searchByName(name, pageable)

        return ResponseEntity.ok(userResponses)
    }
}