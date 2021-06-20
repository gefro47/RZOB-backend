package com.gefro.springbootkotlinRZOBbackend.utilits


import com.gefro.springbootkotlinRZOBbackend.models.Vacation
import com.gefro.springbootkotlinRZOBbackend.models.holidays.CalendarConvert
import com.gefro.springbootkotlinRZOBbackend.models.holidays.Holidays
import com.gefro.springbootkotlinRZOBbackend.models.holidays.NoWork
import com.gefro.springbootkotlinRZOBbackend.models.holidays.Preholidays
import com.gefro.springbootkotlinRZOBbackend.repository.HolidaysRepository
import com.gefro.springbootkotlinRZOBbackend.repository.NotWork2020Repository
import com.gefro.springbootkotlinRZOBbackend.repository.PreholidaysRepository
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.sql.Date
import java.time.temporal.TemporalAdjusters
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

fun checkvacatin(date: Date, list_of_vacation_start: List<Vacation>, list_of_vacation_stop: List<Vacation>): List<Vacation>{

    val LIST_OF_VACATION = mutableListOf<Vacation>()

//    val list_of_vacation_start = vacationRepository.findByYearAndUserStart(date, User(user_id))
//    val list_of_vacation_stop = vacationRepository.findByYearAndUserStop(date, User(user_id))

    for (i in list_of_vacation_start.indices){
        LIST_OF_VACATION.add(list_of_vacation_start[i])
    }
    for (i in list_of_vacation_stop.indices){
        if (!LIST_OF_VACATION.contains(list_of_vacation_stop[i])){
            LIST_OF_VACATION.add(list_of_vacation_stop[i])
        }
    }
    val LIST_OF_VACATION_RETURN = mutableListOf<Vacation>()

    val datemonth = date.toLocalDate().monthValue
    val dateyear = date.toLocalDate().year

    for (i in LIST_OF_VACATION.indices) {
        val monthstart = LIST_OF_VACATION[i].date_start.toLocalDate().monthValue
        val yearstart = LIST_OF_VACATION[i].date_start.toLocalDate().year

        val monthstop = LIST_OF_VACATION[i].date_stop.toLocalDate().monthValue
        val yearstop = LIST_OF_VACATION[i].date_stop.toLocalDate().year

        if (monthstart == datemonth && yearstart == dateyear){
            if (yearstart == yearstop) {
                if ((monthstop - monthstart) == 1) {
                    LIST_OF_VACATION_RETURN.add(LIST_OF_VACATION[i])
                    println("${LIST_OF_VACATION[i].date_start} - ${LIST_OF_VACATION[i].date_stop}")
                }
            }else{
                if ((monthstop - monthstart + 12) == 1) {
                    LIST_OF_VACATION_RETURN.add(LIST_OF_VACATION[i])
                    println("${LIST_OF_VACATION[i].date_start} - ${LIST_OF_VACATION[i].date_stop}")
                }
            }
        }

        if (monthstop == datemonth && yearstop == dateyear){
            if (yearstart == yearstop) {
                if ((monthstop - monthstart) == 1) {
                    LIST_OF_VACATION_RETURN.add(LIST_OF_VACATION[i])
                    println("${LIST_OF_VACATION[i].date_start} - ${LIST_OF_VACATION[i].date_stop}")
                }
            }else{
                if ((monthstop - monthstart + 12) == 1) {
                    LIST_OF_VACATION_RETURN.add(LIST_OF_VACATION[i])
                    println("${LIST_OF_VACATION[i].date_start} - ${LIST_OF_VACATION[i].date_stop}")
                }
            }
        }


        if (yearstart == yearstop){
            if ((monthstop - monthstart) > 1){
                if (date.after(LIST_OF_VACATION[i].date_start) && date.before(LIST_OF_VACATION[i].date_stop)) {
                    LIST_OF_VACATION_RETURN.add(LIST_OF_VACATION[i])
                    println("${LIST_OF_VACATION[i].date_start} - ${LIST_OF_VACATION[i].date_stop}")
                }
            }
        }else{
            if ((monthstop - monthstart+12) > 1){
                if (date.after(LIST_OF_VACATION[i].date_start) && date.before(LIST_OF_VACATION[i].date_stop)) {
                    LIST_OF_VACATION_RETURN.add(LIST_OF_VACATION[i])
                    println("${LIST_OF_VACATION[i].date_start} - ${LIST_OF_VACATION[i].date_stop}")
                }
            }
        }

        if(yearstart == yearstop && monthstop == monthstart){
            LIST_OF_VACATION_RETURN.add(LIST_OF_VACATION[i])
        }

    }

    return LIST_OF_VACATION_RETURN
}

fun checkDatesVacationOfMonth(date: Date, list_of_vacation: List<Vacation>, list_holidays_of_month: List<Date>): Int{
    var list_schet = 0
    val c =Calendar.getInstance()
    val maxDateOfMonth = date.toLocalDate().with(TemporalAdjusters.lastDayOfMonth()).dayOfMonth
    val list_rab_day = mutableListOf<Date>()

    for (y in 0 until maxDateOfMonth){
        val kek_date = Date(Date.valueOf("${date.toLocalDate().year}-${date.toLocalDate().monthValue}-1").time + (one_day*y))
        if(!list_holidays_of_month.contains(kek_date)){
            list_rab_day.add(kek_date)
        }
    }

    for (i in list_of_vacation.indices){
        val start_days = list_of_vacation[i].date_start
        val stop_days = list_of_vacation[i].date_stop
        val schet = (stop_days.time - start_days.time)/ one_day + 1
        val month = date.toLocalDate().monthValue

        for (j in 0..schet.toInt()){
            val date_of_vacation = Date(start_days.time + (one_day * j))
            if (date_of_vacation.toLocalDate().monthValue == month){
                for (k in list_rab_day.indices){
                    if (list_rab_day[k] == date_of_vacation){
                        list_schet ++
                    }
                }
            }
        }
    }

    return list_schet
}


