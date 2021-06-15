package com.gefro.springbootkotlinRZOBbackend.controller

import com.gefro.springbootkotlinRZOBbackend.models.SickLeave
import com.gefro.springbootkotlinRZOBbackend.models.User
import com.gefro.springbootkotlinRZOBbackend.repository.SickLeaveRepository
import com.gefro.springbootkotlinRZOBbackend.repository.UserRepository
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

    @GetMapping("/{user_id}/sickleave/year/{date}")
    fun getSickLeaveByUserIdAndYear(@PathVariable("date") date: String, @PathVariable("user_id") user_id: String): ResponseEntity<List<SickLeave>> {
        val getSickLeaveListByStart = sickLeaveRepository.findByYearAndUserStart(Date.valueOf(date), User(user_id))
        val getSickLeaveListByStop = sickLeaveRepository.findByYearAndUserStop(Date.valueOf(date), User(user_id))
        val returnList = mutableListOf<SickLeave>()

        if (getSickLeaveListByStart.isNotEmpty()){
            for (i in getSickLeaveListByStart.indices){
                returnList.add(getSickLeaveListByStart[i])
            }
        }
        if (getSickLeaveListByStop.isNotEmpty()){
            for (i in getSickLeaveListByStop.indices){
                if (!getSickLeaveListByStart.contains(getSickLeaveListByStop[i])){
                    returnList.add(getSickLeaveListByStop[i])
                }
            }
        }

        if (returnList.isEmpty()){
            return ResponseEntity<List<SickLeave>>(HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity<List<SickLeave>>(returnList, HttpStatus.OK)

    }

    @PostMapping("/sickleave")
    fun addNewSickLeave(@RequestBody sickLeave: SickLeave, uri: UriComponentsBuilder): ResponseEntity<SickLeave>{
        val saveSickLeave = sickLeaveRepository.save(sickLeave)
        if(ObjectUtils.isEmpty(saveSickLeave)){
            return ResponseEntity<SickLeave>(HttpStatus.BAD_REQUEST)
        }
        val headers = HttpHeaders()
        headers.location = uri.path("/sickleave/{id}").buildAndExpand(sickLeave.id).toUri()
        return ResponseEntity(headers, HttpStatus.CREATED)
    }

    @PutMapping("/sickleave/{id}")
    fun updateSickLeaveById(@PathVariable("id") id: Long, @RequestBody sickLeave: SickLeave): ResponseEntity<SickLeave> {
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
    fun removeSickLeaveById(@PathVariable("id") id: Long): ResponseEntity<Void> {
        val sickleave = sickLeaveRepository.findById(id)
        if (sickleave.isPresent) {
            sickLeaveRepository.deleteById(id)
            return ResponseEntity<Void>(HttpStatus.NO_CONTENT)
        }
        return ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}