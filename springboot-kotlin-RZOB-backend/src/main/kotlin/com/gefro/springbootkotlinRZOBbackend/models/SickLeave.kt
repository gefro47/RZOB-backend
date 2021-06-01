package com.gefro.springbootkotlinRZOBbackend.models

import com.fasterxml.jackson.annotation.JsonBackReference
import java.sql.Date
import javax.persistence.*

@Entity
@Table(name = "SICKLEAVE")
data class SickLeave(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var date: Date,
    var days: Int,
    @ManyToOne
    @JoinColumn(name = "user")
    @JsonBackReference
    var user: User? = null
)
