package ru.quipy.entities

import java.util.UUID

data class ProjectMemberEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val login: String,
    val userID: UUID,
    val projectID: UUID,
)
