package com.quotalogic.routes

import com.quotalogic.services.EmployeeService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDate

//TODO Add logger
//TODO Validate user's input
fun Route.employeeRouting(employeeService: EmployeeService) {

    route("/employee") {
        get("/date/{date?}") {
            val date = call.parameters["date"] ?: return@get call.respondText(
                "Missing date",
                status = HttpStatusCode.BadRequest
            )
            val employee = employeeService
                .getEmployeeByDate(LocalDate.parse(date)) ?: return@get call.respondText(
                "Day on $date is free to book",
                status = HttpStatusCode.NotFound
            )
            call.respond(employee)
        }
    }
}
