package ru.quipy.entities

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class TaskStatusEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    var name: String,
    var projectID: UUID,
    var color: Color,
    var priority: Int,
)
