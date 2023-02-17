package com.quotalogic.routes

import com.quotalogic.models.DayOff
import com.quotalogic.services.DayOffService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

//private val logger = KotlinLogging.logger {}

fun Route.dayOffRouting(dayOffService: DayOffService) {
    route("/day-off") {
        post {
            val dayOff = call.receive<DayOff>()
            val res = dayOffService.addDayOff(dayOff.employeeId, dayOff.date)
            if (res.first) {
                call.respond(HttpStatusCode.Created, res.second)
                //logger.info { "Booked ${dayOff.date} for an employee with email: ${dayOff.employeeId}" }
            } else {
                call.respond(HttpStatusCode.BadRequest, res.second)
                //logger.error { "Bad request. Reason: ${res.second}" }
            }
        }
    }
}