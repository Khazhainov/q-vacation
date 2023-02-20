package com.quotalogic.services

import com.quotalogic.models.Employee
import com.quotalogic.plugins.DayOffs
import com.quotalogic.plugins.Employees
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDate
//TODO Add validation for user's input
class EmployeeService {

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun getEmployeeByDate(date: LocalDate): Employee? {
        return dbQuery {
            (Employees innerJoin DayOffs)
                .slice(Employees.columns)
                .select { DayOffs.date eq date }
                .map { row ->
                    Employee(
                        email = row[Employees.email],
                        firstName = row[Employees.firstName],
                        lastName = row[Employees.lastName]
                    )
                }
                .singleOrNull()
        }
    }
}