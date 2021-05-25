package com.gefro.springbootkotlinRZOBbackend.models.holidays

import java.sql.Date
import javax.persistence.*

@Entity
@Table(name = "NOWORK")
data class NoWork(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,
    val date: Date,
    val short: String
)