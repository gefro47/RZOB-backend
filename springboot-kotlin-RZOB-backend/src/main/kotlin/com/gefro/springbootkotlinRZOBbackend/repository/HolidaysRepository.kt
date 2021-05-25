package com.gefro.springbootkotlinRZOBbackend.repository

import com.gefro.springbootkotlinRZOBbackend.models.Recast
import com.gefro.springbootkotlinRZOBbackend.models.User
import com.gefro.springbootkotlinRZOBbackend.models.holidays.Holidays
import com.gefro.springbootkotlinRZOBbackend.models.holidays.NoWork
import com.gefro.springbootkotlinRZOBbackend.models.holidays.Preholidays
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.sql.Date

@Repository
interface HolidaysRepository: JpaRepository <Holidays, Long> {

    @Query("from Holidays where MONTH(date) = MONTH(:date) and YEAR(date) = YEAR(:date)")
    fun findByYearMonth(date: Date): List<Holidays>

//    fun findAllByShort(short: String): List<Holidays>
//    fun findByDate(date: Date): List<Holidays>
}
//
@Repository
interface PreholidaysRepository: JpaRepository <Preholidays, Long> {
}

@Repository
interface NotWork2020Repository: JpaRepository <NoWork, Long> {
}