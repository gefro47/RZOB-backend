package com.gefro.springbootkotlinRZOBbackend.math

import com.gefro.springbootkotlinRZOBbackend.models.Income
import com.gefro.springbootkotlinRZOBbackend.models.Recast
import com.gefro.springbootkotlinRZOBbackend.models.User
import com.gefro.springbootkotlinRZOBbackend.repository.*
import java.math.BigDecimal
import java.sql.Date
import java.time.Year
import java.time.YearMonth
import java.util.concurrent.TimeUnit

class Math(
    private val userRepository: UserRepository,
    private val holidaysRepository: HolidaysRepository,
    private val incomeRepository: IncomeRepository,
    private val recastRepository: RecastRepository,
    private val sickLeaveRepository: SickLeaveRepository,
    private val vacationRepository: VacationRepository
    ){
    fun mathIncome(date: Date, user_id: String): Income {
        val one_day: Long = 1000 * 60 * 60 * 24
        val LIST_OF_RECAST_HOURS_15 = mutableListOf<Double>()
        val LIST_OF_RECAST_HOURS_2 = mutableListOf<Double>()
        val list_holidays_of_month = mutableListOf<Date>()
        val list = mutableListOf<Recast>()
        var schet: Int = 0
//        val short_date = YearMonth.of(date.year, date.month + 1)
//        println(short_date)
        for (element in holidaysRepository.findByYearMonth(date)){
            list_holidays_of_month.add(element.date)
        }
        val rab_day = date.toLocalDate().lengthOfMonth().toBigDecimal().minus(list_holidays_of_month.size.toBigDecimal())
        var salary = 0.0
        val percent = 0.87

        val sick_leave_list = mutableListOf<Date>()
        val sick_leave = sickLeaveRepository.findByYearMonthAndUserStart(date, User(user_id))
        for (i in sick_leave.indices){
            val dif = TimeUnit.DAYS.convert(sick_leave[i].date_stop.time - sick_leave[i].date_start.time, TimeUnit.MILLISECONDS)
            for(j in 0..dif.toInt()){
                sick_leave_list.add(Date(sick_leave[i].date_start.time + j*one_day))
            }
        }
        val vacation_list = mutableListOf<Date>()
        val vacation = vacationRepository.findByYearMonthAndUserStart(date, User(user_id))
        for (i in vacation.indices){
            val dif = TimeUnit.DAYS.convert(vacation[i].date_stop.time - vacation[i].date_start.time, TimeUnit.MILLISECONDS)
            for(j in 0..dif.toInt()){
                vacation_list.add(Date(sick_leave[i].date_start.time + j*one_day))
            }
        }
        for(i in vacation_list.indices){
            if(!list_holidays_of_month.contains(vacation_list[i])){
                schet++
            }
        }
        for(i in sick_leave_list.indices){
            if(!list_holidays_of_month.contains(sick_leave_list[i])){
                schet++
            }
        }

        if (userRepository.getOne(user_id).salary != null) {
            salary = userRepository.getOne(user_id).salary!!
        }
        var income_of_money = salary.toBigDecimal().times(percent.toBigDecimal())
        val income_in_day = income_of_money.div(rab_day)
        val income_in_hours = income_in_day.div(BigDecimal(8))
        val nerabmoney = income_in_day.multiply(schet.toBigDecimal())
        for (element in recastRepository.findByYearMonthAndUser(date, User(user_id))){
            list.add(element)
        }

//        println(list.toString())
//        for (element in userRepository.findById(user_id).get().recast){
//            list.add(element)
//        }
        for (i in 0 until list.size){
            if (list_holidays_of_month.contains(list[i].date)){
                LIST_OF_RECAST_HOURS_2.add(list[i].recasthours)
            }else {
                if (list[i].recasthours > 2) {
                    LIST_OF_RECAST_HOURS_2.add(list[i].recasthours.toBigDecimal().minus(BigDecimal(2)).toDouble())
                    LIST_OF_RECAST_HOURS_15.add(2.0)
                }else{
                    LIST_OF_RECAST_HOURS_15.add(list[i].recasthours)
                }
            }
        }



        val k15 = LIST_OF_RECAST_HOURS_15.sum().toBigDecimal().times(BigDecimal(1.5)).times(income_in_hours)
        val k2 = LIST_OF_RECAST_HOURS_2.sum().toBigDecimal().times(BigDecimal(2)).times(income_in_hours)
        var itog1 = income_of_money.plus(k15.plus(k2))
        var itog = itog1.minus(nerabmoney)
        val getIncome = userRepository.findById(user_id).get().income
        var get_income: Income? = null
        for (i in getIncome.indices){
            if (getIncome[i].month == date.toLocalDate().monthValue &&
                getIncome[i].year == date.toLocalDate().year){
                get_income = getIncome[i]
            }
        }
        if (get_income == null){
            val income = Income(
                year = date.toLocalDate().year,
                month = date.toLocalDate().monthValue,
                income_of_money = itog.toDouble(),
                math_calc = true,
                user = userRepository.getOne(user_id)
            )
            incomeRepository.save(income)
        }else{
            get_income.id?.let { incomeRepository.findById(it).map {
                incomeDetails ->
                val updatedIncome: Income = incomeDetails.copy(
                    income_of_money = itog.toDouble(),
                    math_calc = true
                )
                incomeRepository.save(updatedIncome)
            } }
        }
        return incomeRepository.findByYearMonthAndUser(date, User(user_id))
    }
}