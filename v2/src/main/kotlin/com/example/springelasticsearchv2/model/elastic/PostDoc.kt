package com.example.springelasticsearchv2.model.elastic

import com.example.springelasticsearchv2.entity.enum.PostStatus
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import org.springframework.data.elasticsearch.annotations.Setting


@Document(indexName = "posts")
@Setting(settingPath = "/settings/elasticsearch-settings.json")
class PostDoc(
    @Id //spring data annotation
    @Field(type = FieldType.Long)
    val postId: Long,
    @Field(type = FieldType.Text)
    val title: String? = null,
    @Field(type = FieldType.Keyword)
    val postStatus: PostStatus? = null,
    @Field(type = FieldType.Text)
    val content: String? = null,
    @Field(type = FieldType.Keyword)
    val categoryName: String? = null,
    @Field(type = FieldType.Text)
    val tagName: String? = null, // post.tags.map { it.name }.toList().joinToString { "," }
    @Field(type = FieldType.Date)
    val createdAt: String? = null
)