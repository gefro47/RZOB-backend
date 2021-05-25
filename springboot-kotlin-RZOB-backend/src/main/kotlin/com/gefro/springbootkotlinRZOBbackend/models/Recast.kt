package com.gefro.springbootkotlinRZOBbackend.models

import com.fasterxml.jackson.annotation.JsonBackReference
import java.sql.Date
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "RECAST")
data class Recast(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int,
    var date: Date,
    var recasthours: Double,
    @ManyToOne
    @JoinColumn(name = "user")
    @JsonBackReference
    var user: User? = null
)