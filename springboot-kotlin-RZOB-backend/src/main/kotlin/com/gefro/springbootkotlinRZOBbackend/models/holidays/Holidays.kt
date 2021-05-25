package com.gefro.springbootkotlinRZOBbackend.models.holidays

import java.sql.Date
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "HOLIDAYS")
data class Holidays(
    @Id
    var id: Long?,
    val date: Date,
    val short: String
)