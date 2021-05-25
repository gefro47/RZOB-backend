package com.gefro.springbootkotlinRZOBbackend.controller

import com.gefro.springbootkotlinRZOBbackend.models.Recast
import com.gefro.springbootkotlinRZOBbackend.models.User
import com.gefro.springbootkotlinRZOBbackend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.util.ObjectUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder


@RestController
@RequestMapping(value = ["/api"])
//@Component
class UserController (private val userRepository: UserRepository) {

//    @Autowired
//    lateinit var userRepository: UserRepository

//    @GetMapping("/user")
//    fun getAllUser(): ResponseEntity<List<User>> {
//        val user = userRepository.findAll()
//        if (user.isEmpty()){
//            return ResponseEntity<List<User>>(HttpStatus.NO_CONTENT)
//        }
//        return ResponseEntity<List<User>>(user, HttpStatus.OK)
//    }

    @GetMapping("/user/{id}")
    fun getUserById(@PathVariable("id") id: String): ResponseEntity<User> {
        val user = userRepository.findById(id)
        if (user.isPresent) {
            return ResponseEntity<User>(user.get(), HttpStatus.OK)
        }
        return ResponseEntity<User>(HttpStatus.NOT_FOUND)
    }

    @PostMapping("/user")
    fun addNewUser(@RequestBody user: User, uri: UriComponentsBuilder): ResponseEntity<User>{
        val saveUser = userRepository.save(user)
        if(ObjectUtils.isEmpty(saveUser)){
            return ResponseEntity<User>(HttpStatus.BAD_REQUEST)
        }
        val headers = HttpHeaders()
        headers.location = uri.path("/user/{id}").buildAndExpand(user.id).toUri()
        return ResponseEntity(headers, HttpStatus.CREATED)
    }

    @PutMapping("/user/{id}")
    fun updateUserById(@PathVariable("id") id: String, @RequestBody user: User): ResponseEntity<User> {
        return userRepository.findById(id).map {
                userDetails ->
            val updatedUser: User = userDetails.copy(
                phone = user.phone,
                position = user.position,
                salary = user.salary,
                average_salary = user.average_salary
            )
            ResponseEntity(userRepository.save(updatedUser), HttpStatus.OK)
        }.orElse(ResponseEntity<User>(HttpStatus.INTERNAL_SERVER_ERROR))
    }

    @DeleteMapping("/user/{id}")
    fun removeUserById(@PathVariable("id") id: String): ResponseEntity<Void> {
        val user = userRepository.findById(id)
        if (user.isPresent) {
            userRepository.deleteById(id)
//            getRecastByYearAndMonth(date.split("-")[0].toInt(), date.split("-")[1].toInt())
            return ResponseEntity<Void>(HttpStatus.NO_CONTENT)
        }
        return ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}