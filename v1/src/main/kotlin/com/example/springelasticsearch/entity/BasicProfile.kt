package com.example.springelasticsearch.entity

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
 class BasicProfile (

    @Column(nullable = false, unique = true)
    val name : String = "",
    val description : String = ""

)