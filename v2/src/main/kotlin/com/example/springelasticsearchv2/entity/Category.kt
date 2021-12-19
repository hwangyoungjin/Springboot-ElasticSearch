package com.example.springelasticsearchv2.entity

import javax.persistence.*

@Entity
class Category(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    val name: String = "",

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    var posts: MutableList<Post> = mutableListOf()

) : BaseEntity()