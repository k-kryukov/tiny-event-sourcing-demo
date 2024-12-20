package ru.quipy.entities

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    var name: String = "",
    var description: String = "",
    var projectID: UUID = UUID.randomUUID(),
    var assignees: MutableList<UUID> = mutableListOf(),
    var statusID: UUID = UUID.randomUUID(),
)
