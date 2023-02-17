package com.quotalogic.models

import com.quotalogic.serializers.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class DayOff(@Serializable(LocalDateSerializer::class) val date: LocalDate, val employeeId: String)