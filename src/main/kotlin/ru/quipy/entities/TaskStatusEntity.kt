package ru.quipy.entities

import java.util.UUID

data class TaskStatusEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val projectID: UUID,
    val color: Color,
    var position: Int,
)
