package com.quotalogic.plugins

import com.quotalogic.routes.*
import com.quotalogic.services.DayOffService
import com.quotalogic.services.EmployeeService
import io.ktor.server.routing.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    routing {
        employeeRouting(EmployeeService())
        dayOffRouting(DayOffService())
    }
}
