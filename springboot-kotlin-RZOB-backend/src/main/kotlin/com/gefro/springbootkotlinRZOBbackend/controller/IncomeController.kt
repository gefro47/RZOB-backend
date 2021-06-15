package com.gefro.springbootkotlinRZOBbackend.controller


import com.gefro.springbootkotlinRZOBbackend.math.Math
import com.gefro.springbootkotlinRZOBbackend.models.Income
import com.gefro.springbootkotlinRZOBbackend.repository.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.ObjectUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.sql.Date


@RestController
@RequestMapping(value = ["/api"])
class IncomeController {

    @Autowired
    lateinit var incomeRepository: IncomeRepository

    @Autowired
    lateinit var holidaysRepository: HolidaysRepository

    @Autowired
    lateinit var recastRepository: RecastRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var sickLeaveRepository: SickLeaveRepository

    @Autowired
    lateinit var vacationRepository: VacationRepository

    @GetMapping("{user_id}/income")
    fun getAllIncomeByUserId(@PathVariable("user_id") user_id: String): ResponseEntity<List<Income>> {
        val income = userRepository.findById(user_id).get().income
        if (income.isEmpty()){
            return ResponseEntity<List<Income>>(HttpStatus.NO_CONTENT)
        }
        return ResponseEntity<List<Income>>(income, HttpStatus.OK)
    }

//    @GetMapping("/{user_id}/create_income/{date}")

    @GetMapping("/{user_id}/income/{date}")
    fun getAllIncomeByUserIdAndDate(@PathVariable("date") date: String, @PathVariable("user_id") user_id: String): ResponseEntity<Income>{
//        Math(userRepository, holidaysRepository, incomeRepository).mathIncome(date.split("-")[1].toInt(), date.split("-")[0].toInt(), user_id)
        val getIncome = userRepository.findById(user_id).get().income
        var get_income: Income? = null
        for (i in getIncome.indices){
            if (getIncome[i].month == date.split("-")[1].toInt() &&
                getIncome[i].year == date.split("-")[0].toInt()){
                get_income = getIncome[i]
            }
        }
        if (get_income == null){
//            mathIncome(date.split("-")[1].toInt(), date.split("-")[0].toInt(), user_id)
            return ResponseEntity<Income>(HttpStatus.NO_CONTENT)
        }
//        mathIncome(date.split("-")[1].toInt(), date.split("-")[0].toInt(), user_id)
        return ResponseEntity<Income>(get_income, HttpStatus.OK)
    }

    @GetMapping("calc/{user_id}/income/{date}")
    fun getIncomeCalc(@PathVariable("date") date: Date, @PathVariable("user_id") user_id: String): ResponseEntity<Income>{
        val calc = Math(userRepository, holidaysRepository, incomeRepository, recastRepository, sickLeaveRepository, vacationRepository).mathIncome(date, user_id)
//        val getIncome = userRepository.findById(user_id).get().income
//        var get_income: Income? = null
//        for (i in getIncome.indices){
//            if (getIncome[i].month == date.split("-")[1].toInt() &&
//                getIncome[i].year == date.split("-")[0].toInt()){
//                get_income = getIncome[i]
//            }
//        }
//        if (get_income == null){
////            mathIncome(date.split("-")[1].toInt(), date.split("-")[0].toInt(), user_id)
//            return ResponseEntity<Income>(HttpStatus.NO_CONTENT)
//        }
//        mathIncome(date.split("-")[1].toInt(), date.split("-")[0].toInt(), user_id)
        return ResponseEntity<Income>(calc, HttpStatus.OK)
    }

    @PostMapping("/income")
    fun addNewIncome(@RequestBody income: Income, uri: UriComponentsBuilder): ResponseEntity<Income>{
        val saveIncome = incomeRepository.save(income)
        if(ObjectUtils.isEmpty(saveIncome)){
            return ResponseEntity<Income>(HttpStatus.BAD_REQUEST)
        }
        val headers = HttpHeaders()
        headers.location = uri.path("/income/{id}").buildAndExpand(income.id).toUri()
        return ResponseEntity(headers, HttpStatus.CREATED)
    }

    @PutMapping("/income/{id}")
    fun updateIncomeById(@PathVariable("id") id: Long, @RequestBody income: Income): ResponseEntity<Income> {
        return incomeRepository.findById(id).map {
                incomeDetails ->
            val updatedIncome: Income = incomeDetails.copy(
                income_of_money = income.income_of_money,
                math_calc = income.math_calc
            )
            ResponseEntity(incomeRepository.save(updatedIncome), HttpStatus.OK)
        }.orElse(ResponseEntity<Income>(HttpStatus.INTERNAL_SERVER_ERROR))
    }

    @DeleteMapping("/income/{id}")
    fun removeIncomeById(@PathVariable("id") id: Long): ResponseEntity<Void> {
        val income = incomeRepository.findById(id)
        if (income.isPresent) {
            incomeRepository.deleteById(id)
            return ResponseEntity<Void>(HttpStatus.NO_CONTENT)
        }
        return ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}