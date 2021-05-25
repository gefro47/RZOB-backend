package com.gefro.springbootkotlinRZOBbackend.repository

import com.gefro.springbootkotlinRZOBbackend.models.Income
import com.gefro.springbootkotlinRZOBbackend.models.Recast
import com.gefro.springbootkotlinRZOBbackend.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.sql.Date

@Repository
interface IncomeRepository: JpaRepository<Income, Long> {
//    fun findByMonthAndYear(month: Int, year: Int): Income
    @Query("from Income where MONTH = MONTH(:date) and YEAR = YEAR(:date) and user = :user")
    fun findByYearMonthAndUser(date: Date, user: User): Income
}