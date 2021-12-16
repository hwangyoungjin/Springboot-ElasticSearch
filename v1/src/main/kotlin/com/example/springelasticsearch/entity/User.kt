package com.example.springelasticsearch.entity

import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.sql.Date
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*


@Document(indexName = "users")
@Entity
class User (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Embedded
    var basicProfile: BasicProfile? = null,

    @Field(type = FieldType.Date, format = [], pattern = ["uuuu-MM-dd HH:mm:ss"])
    var modificationDate : LocalDateTime? = null

)