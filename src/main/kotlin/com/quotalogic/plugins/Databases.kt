package com.quotalogic.plugins

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

//TODO Add logging
fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        driver = "org.h2.Driver",
        user = "root",
        password = ""
    )

    transaction(database) {
        SchemaUtils.create(Employees, DayOffs)

        Employees.insert {
            it[email] = "johndoe@email.com"
            it[firstName] = "John"
            it[lastName] = "Doe"
        }

        Employees.insert {
            it[email] = "janesmith@email.com"
            it[firstName] = "Jane"
            it[lastName] = "Smith"
        }

        Employees.insert {
            it[email] = "bobjohnson@email.com"
            it[firstName] = "Bob"
            it[lastName] = "Johnson"
        }

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
            it[this.date] = LocalDate.now()
            it[employeeId] = "davidlee@email.com"
        }

    }
}

object Employees : Table() {
    val email = varchar("email", 50)
    val firstName = varchar("name", 50)
    val lastName = varchar("last_name", 50)

    override val primaryKey = PrimaryKey(email, name = "PK_Employee_ID")
}

object DayOffs : Table() {
    val date = date("date")
    val employeeId = varchar("employee_id", 50) references Employees.email

    override val primaryKey = PrimaryKey(date, name = "PK_DayOff_ID")
}