package com.gefro.springbootkotlinRZOBbackend.controller

import com.gefro.springbootkotlinRZOBbackend.models.Income
import com.gefro.springbootkotlinRZOBbackend.models.Recast
import com.gefro.springbootkotlinRZOBbackend.models.SickLeave
import com.gefro.springbootkotlinRZOBbackend.models.User
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
class SickLeaveController {

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

    @GetMapping("/{user_id}/sickleave/{date}")
    fun getSickLeaveByUserIdAndMonthYear(@PathVariable("date") date: Date, @PathVariable("user_id") user_id: String): ResponseEntity<List<SickLeave>> {
//        Math(userRepository, holidaysRepository, incomeRepository).mathIncome(date.split("-")[1].toInt(), date.split("-")[0].toInt(), user_id)
        val month = SimpleDateFormat("yyyy-MM-dd").format(date).toString().split("-")[1]
        val year = SimpleDateFormat("yyyy-MM-dd").format(date).toString().split("-")[0]
        val getSickLeaveList = userRepository.findById(user_id).get().sickLeave
        val returnList = mutableListOf<SickLeave>()

        for (i in getSickLeaveList.indices){
            val getMonthStart = SimpleDateFormat("yyyy-MM-dd").format(getSickLeaveList[i].date_start).toString().split("-")[1]
            val getMonthStop = SimpleDateFormat("yyyy-MM-dd").format(getSickLeaveList[i].date_stop).toString().split("-")[1]
            val getYearStart = SimpleDateFormat("yyyy-MM-dd").format(getSickLeaveList[i].date_start).toString().split("-")[0]
            val getYearStop = SimpleDateFormat("yyyy-MM-dd").format(getSickLeaveList[i].date_stop).toString().split("-")[0]
            if(getMonthStart == month && getYearStart == year){
                returnList.add(getSickLeaveList[i])
            }else if (getMonthStop == month && getYearStop == year){
                returnList.add(getSickLeaveList[i])
            }
        }

        if (returnList.isEmpty()){
            return ResponseEntity<List<SickLeave>>(HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity<List<SickLeave>>(returnList, HttpStatus.OK)

    }

    @PostMapping("/sickleave")
    fun addNewRecast(@RequestBody sickLeave: SickLeave, uri: UriComponentsBuilder): ResponseEntity<SickLeave>{
        val saveSickLeave = sickLeaveRepository.save(sickLeave)
        if(ObjectUtils.isEmpty(saveSickLeave)){
            return ResponseEntity<SickLeave>(HttpStatus.BAD_REQUEST)
        }
        val headers = HttpHeaders()
        headers.location = uri.path("/sickleave/{id}").buildAndExpand(sickLeave.id).toUri()
        return ResponseEntity(headers, HttpStatus.CREATED)
    }

    @PutMapping("/sickleave/{id}")
    fun updateRecastById(@PathVariable("id") id: Long, @RequestBody sickLeave: SickLeave): ResponseEntity<SickLeave> {
        return sickLeaveRepository.findById(id).map {
                sickLeaveDetails ->
            val updatedSickLeave: SickLeave = sickLeaveDetails.copy(
                date_start = sickLeave.date_start,
                date_stop = sickLeave.date_stop
            )
            ResponseEntity(sickLeaveRepository.save(updatedSickLeave), HttpStatus.OK)
        }.orElse(ResponseEntity<SickLeave>(HttpStatus.INTERNAL_SERVER_ERROR))
    }

    @DeleteMapping("/sickleave/{id}")
    fun removeRecastById(@PathVariable("id") id: Long): ResponseEntity<Void> {
        val sickleave = sickLeaveRepository.findById(id)
        if (sickleave.isPresent) {
            sickLeaveRepository.deleteById(id)
//            getRecastByYearAndMonth(date.split("-")[0].toInt(), date.split("-")[1].toInt())
            return ResponseEntity<Void>(HttpStatus.NO_CONTENT)
        }
        return ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}