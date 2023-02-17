package com.quotalogic

import com.quotalogic.models.Employee
import com.quotalogic.plugins.DayOffs
import com.quotalogic.plugins.Employees
import com.quotalogic.services.DayOffService
import com.quotalogic.services.EmployeeService
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.After
import org.junit.Before
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ApplicationTest {

    private val employeeService = EmployeeService()
    private val dayOffService = DayOffService()
    private lateinit var db: Database

    @Before
    fun setUpDB() {
        db = Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            driver = "org.h2.Driver"
        )

        transaction(db) {
            SchemaUtils.create(Employees, DayOffs)

            Employees.insert {
                it[email] = "sarahwilliams@email.com"
                it[firstName] = "Sarah"
                it[lastName] = "Williams"
            }

            Employees.insert {
                it[email] = "davidlee@email.com"
                it[firstName] = "David"
                it[lastName] = "Lee"
            }

            DayOffs.insert {
                it[this.date] = LocalDate.parse("2023-02-17")
                it[employeeId] = "davidlee@email.com"
            }

        }
    }

    @After
    fun tearDown() {
        transaction(db) {
            SchemaUtils.drop(Employees, DayOffs)
        }
    }

    @Test
    fun `getEmployeeByDate should return an employee when at least one employee has day off on given date`() = runBlocking {
        val result = employeeService.getEmployeeByDate(LocalDate.parse("2023-02-17"))
        assertEquals(Employee("davidlee@email.com", "David", "Lee"), result)
    }

    @Test
    fun `getEmployeeByDate should return null when no employees have day off on given date`() = runBlocking {
        assertNull(employeeService.getEmployeeByDate(LocalDate.parse("2024-02-17")))
    }

    @Test
    fun `addDayOff should return information that the day is already booked if it is so`() = runBlocking {
        val result = dayOffService.addDayOff("davidlee@email.com", LocalDate.parse("2023-02-17"))
        assertEquals(result, Pair(false, "Day already booked"))
    }

    @Test
    fun `addDayOff should return information that the email is incorrect if no employee exists with that email`() = runBlocking {
        val result = dayOffService.addDayOff("rogu@email.com", LocalDate.parse("2023-02-17"))
        assertEquals(result, Pair(false, "Invalid email"))
    }
}

