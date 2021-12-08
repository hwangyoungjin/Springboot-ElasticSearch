package com.example.springelasticsearch.entity

import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.elasticsearch.annotations.Document
import javax.persistence.*


@Document(indexName = "users")
@Entity
class User (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Embedded
    var basicProfile: BasicProfile? = null

)