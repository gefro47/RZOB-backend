package com.gefro.springbootkotlinRZOBbackend.models

import com.fasterxml.jackson.annotation.JsonBackReference
import javax.persistence.*

@Entity
@Table(name = "INCOME")
data class Income(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var year: Int,
    var month: Int,
    var income_of_money: Double,
    var math_calc: Boolean,
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    var user: User? = null
//
)