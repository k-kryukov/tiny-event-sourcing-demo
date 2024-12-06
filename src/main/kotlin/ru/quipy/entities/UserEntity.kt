package ru.quipy.entities

import java.util.UUID

data class UserEntity(
    val id: UUID = UUID.randomUUID(),
    val login: String,
    val name: String,
    val password: String,
)