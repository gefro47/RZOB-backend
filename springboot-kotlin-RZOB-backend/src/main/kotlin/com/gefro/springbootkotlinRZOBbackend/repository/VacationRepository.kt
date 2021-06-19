package com.gefro.springbootkotlinRZOBbackend.repository

import com.gefro.springbootkotlinRZOBbackend.models.SickLeave
import com.gefro.springbootkotlinRZOBbackend.models.User
import com.gefro.springbootkotlinRZOBbackend.models.Vacation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.sql.Date

interface VacationRepository: JpaRepository<Vacation, Long> {

    @Query("from Vacation where MONTH(date_start) = MONTH(:date_start) and YEAR(date_start) = YEAR(:date_start) and user = :user")
    fun findByYearMonthAndUserStart(date_start: Date, user: User): List<Vacation>

    @Query("from Vacation where MONTH(date_stop) = MONTH(:date_stop) and YEAR(date_stop) = YEAR(:date_stop) and user = :user")
    fun findByYearMonthAndUserStop(date_stop: Date, user: User): List<Vacation>

    @Query("from Vacation where YEAR(date_start) = YEAR(:date_start) and user = :user")
    fun findByYearAndUserStart(date_start: Date, user: User): List<Vacation>

    @Query("from Vacation where YEAR(date_stop) = YEAR(:date_stop) and user = :user")
    fun findByYearAndUserStop(date_stop: Date, user: User): List<Vacation>
}