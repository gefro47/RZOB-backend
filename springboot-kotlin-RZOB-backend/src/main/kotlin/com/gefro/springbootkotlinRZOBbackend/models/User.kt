package com.gefro.springbootkotlinRZOBbackend.models

import com.fasterxml.jackson.annotation.JsonManagedReference
import javax.persistence.*

@Entity
@Table(name = "USER")
data class User(
    @Id
    var id: String,
    var phone: String? = "",
    var position: String? = "",
    var salary: Double? = 0.0,
    var average_salary: Double? = 0.0,
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "user", orphanRemoval = true)
    @JsonManagedReference
    var income: List<Income> = emptyList(),
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "user", orphanRemoval = true)
    @JsonManagedReference
    var recast: List<Recast> = emptyList(),
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "user", orphanRemoval = true)
    @JsonManagedReference
    var sickLeave: List<SickLeave> = emptyList(),
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "user", orphanRemoval = true)
    @JsonManagedReference
    var vacation: List<Vacation> = emptyList()
)