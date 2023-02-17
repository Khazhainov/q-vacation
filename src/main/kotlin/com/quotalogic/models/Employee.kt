package com.quotalogic.models

import kotlinx.serialization.Serializable

@Serializable
data class Employee(val email: String, val firstName: String, val lastName: String)



