package com.gefro.springbootkotlinRZOBbackend.utilits


import com.gefro.springbootkotlinRZOBbackend.models.holidays.*
import com.gefro.springbootkotlinRZOBbackend.repository.HolidaysRepository
import com.gefro.springbootkotlinRZOBbackend.repository.NotWork2020Repository
import com.gefro.springbootkotlinRZOBbackend.repository.PreholidaysRepository
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.sql.Date
import java.util.*

@Component
class ReadDateOfCalendar(
//    private val holidaysRepository: HolidaysRepository
//    private val preholidaysRepository: PreholidaysRepository,
//    private val notWork2020Repository: NotWork2020Repository
){
    @Autowired
	lateinit var holidaysRepository: HolidaysRepository

    @Autowired
    lateinit var preholidaysRepository: PreholidaysRepository

    @Autowired
    lateinit var noWorkRepository: NotWork2020Repository

    fun read_date_api() {
//        val database = Database.connect("jdbc:h2:file:~/rzob/db", user = "sa", password = "sa")
//        val holrepo = holidaysRepository
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://raw.githubusercontent.com/d10xa/holidays-calendar/master/json/calendar.json")
            .build()
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val kek = response.body?.string()
//                println(kek)
                val calendar1 = Gson().fromJson(kek, CalendarConvert::class.java)
                for(element in calendar1.holidays){
                    holidaysRepository.save(Holidays(
                        calendar1.holidays.indexOf(element).toLong(),
                        Date.valueOf(element),
                        "${element.split("-")[0]}-${element.split("-")[1]}"
                    ))
                }
                for(element in calendar1.preholidays){
                    preholidaysRepository.save(Preholidays(
                        calendar1.preholidays.indexOf(element).toLong(),
                        Date.valueOf(element),
                        "${element.split("-")[0]}-${element.split("-")[1]}"
                    ))
                }
                for(element in calendar1.nowork){
                    noWorkRepository.save(NoWork(
                        calendar1.nowork.indexOf(element).toLong(),
                        Date.valueOf(element),
                        "${element.split("-")[0]}-${element.split("-")[1]}"
                    ))
                }
            }
        }
    }

    fun read_date_api_everyday() {
        val timer = Timer()
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                read_date_api()
            }
        }
//	timer.schedule(task, 0L, 1000 * 10)
        timer.schedule(task, 0L, 1000 * 60 * 60 * 24)// repeat every 24 hour
    }
}


