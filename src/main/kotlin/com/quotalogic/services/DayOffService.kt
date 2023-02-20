package com.quotalogic.services

import com.quotalogic.plugins.DayOffs
import com.quotalogic.plugins.Employees
import kotlinx.coroutines.Dispatchers
import mu.KotlinLogging
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDate

private val logger = KotlinLogging.logger {}
//TODO Add validation for user inputs
class DayOffService {

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun addDayOff(email: String, date: LocalDate): Pair<Boolean, String> = dbQuery {
        val employeeExists = Employees.select { Employees.email eq email }.count() == 1L
        if (!employeeExists) {
            return@dbQuery false to "Invalid email"
        }

        val dayOffExists = DayOffs.select { DayOffs.date eq date }.count() == 1L
        if (dayOffExists) {
            return@dbQuery false to "Day already booked"
        }

        DayOffs.insert {
            it[employeeId] = email
            it[this.date] = date
        }

        return@dbQuery true to "Day off booked correctly"
    }
}