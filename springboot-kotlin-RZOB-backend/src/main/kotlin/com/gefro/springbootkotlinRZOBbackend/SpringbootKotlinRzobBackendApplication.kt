package com.gefro.springbootkotlinRZOBbackend

import com.gefro.springbootkotlinRZOBbackend.utilits.ReadDateOfCalendar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class SpringbootKotlinRzobBackendApplication


	fun main(args: Array<String>) {
//		read_date_api()
//		runApplication<SpringbootKotlinRzobBackendApplication>(*args)
		val context = runApplication<SpringbootKotlinRzobBackendApplication>(*args)
		val start = context.getBean(ReadDateOfCalendar::class.java)
		start.read_date_api_everyday()
	}