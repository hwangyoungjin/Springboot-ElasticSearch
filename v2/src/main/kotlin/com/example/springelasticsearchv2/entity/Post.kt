package com.example.springelasticsearchv2.entity

import com.example.springelasticsearchv2.entity.enum.PostStatus
import javax.persistence.*

@Entity
class Post(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    var title: String = "",

    var content: String = "",

    val status: PostStatus = PostStatus.DRAFT,

    @JoinColumn(name = "category_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var category: Category? = null,

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    var tags: MutableList<Tag> = mutableListOf(),

) : BaseEntity() {
    fun addTag(tag: Tag){
        this.tags.add(tag)
        tag.post = this
    }

    fun updateCategory(category: Category?){
        //기존에 카테고리있다면 해당 카테고리에서 post 삭제
        if(this.category != null) this.category!!.posts.remove(this)
        //카테고리 변경
        this.category = category
        category?.posts?.add(this)
    }
}