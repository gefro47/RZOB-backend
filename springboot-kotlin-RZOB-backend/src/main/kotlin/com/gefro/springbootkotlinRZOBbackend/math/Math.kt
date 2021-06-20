package com.gefro.springbootkotlinRZOBbackend.math

import com.gefro.springbootkotlinRZOBbackend.models.*
import com.gefro.springbootkotlinRZOBbackend.repository.*
import com.gefro.springbootkotlinRZOBbackend.utilits.checkDatesVacationOfMonth
import com.gefro.springbootkotlinRZOBbackend.utilits.checkvacatin
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
//        val one_day: Long = 1000 * 60 * 60 * 24
        val LIST_OF_RECAST_HOURS_15 = mutableListOf<Double>()
        val LIST_OF_RECAST_HOURS_2 = mutableListOf<Double>()
        val list_holidays_of_month = mutableListOf<Date>()
        val list = mutableListOf<Recast>()

        val LIST_OF_VACATION = checkvacatin(date, vacationRepository.findByYearAndUserStart(date, User(user_id)),vacationRepository.findByYearAndUserStop(date, User(user_id)))


        for (element in holidaysRepository.findByYearMonth(date)){
            list_holidays_of_month.add(element.date)
        }

        val schet_vacation = checkDatesVacationOfMonth(date, LIST_OF_VACATION, list_holidays_of_month)

        val rab_day = date.toLocalDate().lengthOfMonth().toBigDecimal().minus(list_holidays_of_month.size.toBigDecimal())
        var salary = 0.0
        val percent = 0.87


        if (userRepository.getOne(user_id).salary != null) {
            salary = userRepository.getOne(user_id).salary!!
        }
        var income_of_money = salary.toBigDecimal().times(percent.toBigDecimal())
        val income_in_day = income_of_money.div(rab_day)
        val income_in_hours = income_in_day.div(BigDecimal(8))
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
        val itog1 = income_of_money.plus(k15.plus(k2))
        val itog = itog1.minus(income_in_day.times(schet_vacation.toBigDecimal()))
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

        println(schet_vacation)
        return incomeRepository.findByYearMonthAndUser(date, User(user_id))
    }
}




