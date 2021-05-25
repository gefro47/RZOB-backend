package com.gefro.springbootkotlinRZOBbackend.repository

import com.gefro.springbootkotlinRZOBbackend.models.Recast
import com.gefro.springbootkotlinRZOBbackend.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.sql.Date
import java.util.*

@Repository
interface RecastRepository : JpaRepository <Recast, Int>
{
    fun findByUser(user: User): List<Recast>

//    @Query("from Recast where MONTH(date) = MONTH(:date) and YEAR(date) = YEAR(:date)")
    @Query("from Recast where YEAR(date) = YEAR(:date) and user = :user")
    fun findByYearAndUser(date: Date, user: User): List<Recast>

    @Query("from Recast where MONTH(date) = MONTH(:date) and YEAR(date) = YEAR(:date) and user = :user")
    fun findByYearMonthAndUser(date: Date, user: User): List<Recast>
}
