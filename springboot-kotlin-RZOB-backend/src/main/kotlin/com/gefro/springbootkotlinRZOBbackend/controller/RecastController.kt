package com.gefro.springbootkotlinRZOBbackend.controller

//import com.gefro.springbootkotlinRZOBbackend.math.mathRecast
import com.gefro.springbootkotlinRZOBbackend.models.Recast
import com.gefro.springbootkotlinRZOBbackend.models.User
import com.gefro.springbootkotlinRZOBbackend.repository.RecastRepository
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
class RecastController {

    @Autowired
    lateinit var recastRepository: RecastRepository

    @Autowired
    lateinit var userRepository: UserRepository


    @GetMapping("/{user_id}/recasts")
    fun getAllRecastByUserId(@PathVariable("user_id") user_id: String): ResponseEntity<List<Recast>>{
        val recast = userRepository.findById(user_id).get().recast
        if (recast.isEmpty()){
            return ResponseEntity<List<Recast>>(HttpStatus.NO_CONTENT)
        }
        return ResponseEntity<List<Recast>>(recast, HttpStatus.OK)
    }

//    @GetMapping("/recasts/{date}")
//    fun getRecastById(@PathVariable("date") date: String): ResponseEntity<Recast> {
//        val recast = recastRepository.findById(date)
//        if (recast.isPresent) {
//            return ResponseEntity<Recast>(recast.get(), HttpStatus.OK)
//        }
//        return ResponseEntity<Recast>(HttpStatus.NOT_FOUND)
//    }

    @GetMapping("/{user_id}/recasts/{date}")
    fun getAllRecastByUserIdAndDate(@PathVariable("date") date1: String, @PathVariable("user_id") user_id: String): ResponseEntity<List<Recast>>{
        val date = SimpleDateFormat("yyyy-MM-dd").parse(date1)
        val list = mutableListOf<Recast>()
        val getRecast = userRepository.findById(user_id).get().recast
        for (i in getRecast.indices){
            if (
                SimpleDateFormat("yyyy-MM-dd").format(getRecast[i].date) == date1
//                getRecast[i].date.month == date.month &&
//                getRecast[i].date.year == date.year
            ){
                list.add(getRecast[i])
            }
        }
        if (list.isEmpty()){
            return ResponseEntity<List<Recast>>(HttpStatus.NO_CONTENT)
        }
        return ResponseEntity<List<Recast>>(list, HttpStatus.OK)
    }


//    @GetMapping("/{user_id}/recasts/year/{year}")
//    fun getAllRecastByUserIdAndYear(@PathVariable("year") year: String, @PathVariable("user_id") user_id: String): ResponseEntity<List<Recast>>{
////        val date = SimpleDateFormat("yyyy-MM-dd").parse(date1)
//        val list = mutableListOf<Recast>()
//        val getRecast = userRepository.findById(user_id).get().recast
//        for (i in getRecast.indices){
//            if (
//                SimpleDateFormat("yyyy").format(getRecast[i].date) == year
////                getRecast[i].date.month == date.month &&
////                getRecast[i].date.year == date.year
//            ){
//                list.add(getRecast[i])
//            }
//        }
//        if (list.isEmpty()){
//            return ResponseEntity<List<Recast>>(HttpStatus.NO_CONTENT)
//        }
//        return ResponseEntity<List<Recast>>(list, HttpStatus.OK)
//    }

    @GetMapping("/{user_id}/recasts/calendarfragment/year/{date}")
    fun getRecastsForCalendarFragmentByUserAndYear(@PathVariable("date") date: Date, @PathVariable("user_id") user_id: String): ResponseEntity<List<Recast>> {
        val user = User(id = user_id)
        val recast = recastRepository.findByYearAndUser(date, user)
        if (recast.isNotEmpty()){
            return ResponseEntity<List<Recast>>(recast, HttpStatus.OK)
        }
        return ResponseEntity<List<Recast>>(HttpStatus.NOT_FOUND)
    }

    @GetMapping("/{user_id}/recasts/test/yearmonth/{date}")
    fun getRecastsForCalendarFragmentByUserYearAndMonth(@PathVariable("date") date: Date, @PathVariable("user_id") user_id: String): ResponseEntity<List<Recast>> {
        val user = User(id = user_id)
        val recast = recastRepository.findByYearMonthAndUser(date, user)
        if (recast.isNotEmpty()){
            return ResponseEntity<List<Recast>>(recast, HttpStatus.OK)
        }
        return ResponseEntity<List<Recast>>(HttpStatus.NOT_FOUND)
    }


    @PostMapping("/recasts")
    fun addNewRecast(@RequestBody recast: Recast, uri: UriComponentsBuilder): ResponseEntity<Recast>{
        val saveRecast = recastRepository.save(recast)
        if(ObjectUtils.isEmpty(saveRecast)){
            return ResponseEntity<Recast>(HttpStatus.BAD_REQUEST)
        }
        val headers = HttpHeaders()
        headers.location = uri.path("/recasts/{id}").buildAndExpand(recast.id).toUri()
        return ResponseEntity(headers, HttpStatus.CREATED)
    }

    @PutMapping("/recasts/{id}")
    fun updateRecastById(@PathVariable("id") id: Int, @RequestBody recast: Recast): ResponseEntity<Recast> {
        return recastRepository.findById(id).map {
                recastDetails ->
            val updatedRecast: Recast = recastDetails.copy(
                recasthours = recast.recasthours
            )
            ResponseEntity(recastRepository.save(updatedRecast), HttpStatus.OK)
        }.orElse(ResponseEntity<Recast>(HttpStatus.INTERNAL_SERVER_ERROR))
    }

    @DeleteMapping("/recasts/{id}")
    fun removeRecastById(@PathVariable("id") id: Int): ResponseEntity<Void> {
        val recast = recastRepository.findById(id)
        if (recast.isPresent) {
            recastRepository.deleteById(id)
//            getRecastByYearAndMonth(date.split("-")[0].toInt(), date.split("-")[1].toInt())
            return ResponseEntity<Void>(HttpStatus.NO_CONTENT)
        }
        return ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

//    @DeleteMapping("/recasts")
//    fun removeAllRecasts(): ResponseEntity<Void> {
//        recastRepository.deleteAll()
//        return ResponseEntity<Void>(HttpStatus.OK)
//    }

}