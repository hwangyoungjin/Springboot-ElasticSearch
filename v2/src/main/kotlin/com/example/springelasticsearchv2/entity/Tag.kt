package com.example.springelasticsearchv2.entity

import javax.persistence.*

@Entity
class Tag(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    var name: String = "",

    @JoinColumn(name = "post_id")
    @ManyToOne
    var post: Post? = null

) : BaseEntity()