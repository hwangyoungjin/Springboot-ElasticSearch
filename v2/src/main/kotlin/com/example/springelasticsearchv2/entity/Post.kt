package com.example.springelasticsearchv2.entity

import com.example.springelasticsearchv2.entity.enum.PostStatus
import javax.persistence.*

@Entity
class Post(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    var title: String? = null,

    var content: String? = null,

    val status: PostStatus = PostStatus.DRAFT,

    @JoinColumn(name = "category_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var category: Category? = null,

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    var tags: MutableList<Tag> = mutableListOf(),

) : BaseEntity()