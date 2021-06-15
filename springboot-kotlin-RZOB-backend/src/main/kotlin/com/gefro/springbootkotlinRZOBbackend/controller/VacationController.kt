package com.gefro.springbootkotlinRZOBbackend.controller

import com.gefro.springbootkotlinRZOBbackend.models.SickLeave
import com.gefro.springbootkotlinRZOBbackend.models.User
import com.gefro.springbootkotlinRZOBbackend.models.Vacation
import com.gefro.springbootkotlinRZOBbackend.repository.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.ObjectUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.sql.Date
import java.text.SimpleDateFormat


@RestController
@RequestMapping(value = ["/api"])
class VacationController {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var vacationRepository: VacationRepository


    @GetMapping("/{user_id}/vacation/{date}")
    fun getVacationByUserIdAndMonthYear(@PathVariable("date") date: Date, @PathVariable("user_id") user_id: String): ResponseEntity<List<Vacation>> {
//        Math(userRepository, holidaysRepository, incomeRepository).mathIncome(date.split("-")[1].toInt(), date.split("-")[0].toInt(), user_id)
        val month = SimpleDateFormat("yyyy-MM-dd").format(date).toString().split("-")[1]
        val year = SimpleDateFormat("yyyy-MM-dd").format(date).toString().split("-")[0]
        val getVacationList = userRepository.findById(user_id).get().vacation
        val returnList = mutableListOf<Vacation>()

        for (i in getVacationList.indices){
            val getMonthStart = SimpleDateFormat("yyyy-MM-dd").format(getVacationList[i].date_start).toString().split("-")[1]
            val getMonthStop = SimpleDateFormat("yyyy-MM-dd").format(getVacationList[i].date_stop).toString().split("-")[1]
            val getYearStart = SimpleDateFormat("yyyy-MM-dd").format(getVacationList[i].date_start).toString().split("-")[0]
            val getYearStop = SimpleDateFormat("yyyy-MM-dd").format(getVacationList[i].date_stop).toString().split("-")[0]
            if(getMonthStart == month && getYearStart == year){
                returnList.add(getVacationList[i])
            }else if (getMonthStop == month && getYearStop == year){
                returnList.add(getVacationList[i])
            }
        }

        if (returnList.isEmpty()){
            return ResponseEntity<List<Vacation>>(HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity<List<Vacation>>(returnList, HttpStatus.OK)

    }

    @GetMapping("/{user_id}/vacation/year/{date}")
    fun getVacationByUserIdAndYear(@PathVariable("date") date: String, @PathVariable("user_id") user_id: String): ResponseEntity<List<Vacation>> {
        val getVacationListByStart = vacationRepository.findByYearAndUserStart(Date.valueOf(date), User(user_id))
        val getVacationListByStop = vacationRepository.findByYearAndUserStop(Date.valueOf(date), User(user_id))
        val returnList = mutableListOf<Vacation>()

        if (getVacationListByStart.isNotEmpty()){
            for (i in getVacationListByStart.indices){
                returnList.add(getVacationListByStart[i])
            }
        }
        if (getVacationListByStop.isNotEmpty()){
            for (i in getVacationListByStop.indices){
                if (!getVacationListByStart.contains(getVacationListByStop[i])){
                    returnList.add(getVacationListByStop[i])
                }
            }
        }

        if (returnList.isEmpty()){
            return ResponseEntity<List<Vacation>>(HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity<List<Vacation>>(returnList, HttpStatus.OK)

    }

    @PostMapping("/vacation")
    fun addNewVacation(@RequestBody vacation: Vacation, uri: UriComponentsBuilder): ResponseEntity<Vacation> {
        val saveVacation = vacationRepository.save(vacation)
        if(ObjectUtils.isEmpty(saveVacation)){
            return ResponseEntity<Vacation>(HttpStatus.BAD_REQUEST)
        }
        val headers = HttpHeaders()
        headers.location = uri.path("/vacation/{id}").buildAndExpand(vacation.id).toUri()
        return ResponseEntity(headers, HttpStatus.CREATED)
    }

    @PutMapping("/vacation/{id}")
    fun updateVacationById(@PathVariable("id") id: Long, @RequestBody sickLeave: SickLeave): ResponseEntity<Vacation> {
        return vacationRepository.findById(id).map {
                vacationDetails ->
            val updatedVacation: Vacation = vacationDetails.copy(
                date_start = sickLeave.date_start,
                date_stop = sickLeave.date_stop
            )
            ResponseEntity(vacationRepository.save(updatedVacation), HttpStatus.OK)
        }.orElse(ResponseEntity<Vacation>(HttpStatus.INTERNAL_SERVER_ERROR))
    }

    @DeleteMapping("/vacation/{id}")
    fun removeVacationById(@PathVariable("id") id: Long): ResponseEntity<Void> {
        val vacation = vacationRepository.findById(id)
        if (vacation.isPresent) {
            vacationRepository.deleteById(id)
            return ResponseEntity<Void>(HttpStatus.NO_CONTENT)
        }
        return ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}