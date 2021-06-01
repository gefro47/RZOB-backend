package com.gefro.springbootkotlinRZOBbackend.controller

import com.gefro.springbootkotlinRZOBbackend.repository.HolidaysRepository
import com.gefro.springbootkotlinRZOBbackend.repository.IncomeRepository
import com.gefro.springbootkotlinRZOBbackend.repository.RecastRepository
import com.gefro.springbootkotlinRZOBbackend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


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
}