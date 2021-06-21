package com.gefro.springbootkotlinRZOBbackend.models

import com.fasterxml.jackson.annotation.JsonBackReference
import java.sql.Date
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
    var date_of_avans: Date,
    var avans: Double? = null,
    var date_of_income_without_avans: Date,
    var income_without_avans: Double? = null,
    var math_calc: Boolean,
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    var user: User? = null
//
)