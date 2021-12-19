package com.example.springelasticsearchv2.entity.enum

enum class PostStatus(
    val code:String,
    val korean:String
) {
    PUBLISH("Publish","공개"),
    DRAFT("Draft","작성중"),
}