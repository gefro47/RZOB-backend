package com.gefro.springbootkotlinRZOBbackend.models.holidays

import java.sql.Date
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "PREHOLIDAYS")
data class Preholidays (
    @Id
    var id: Long?,
    val date: Date,
    val short: String
)
