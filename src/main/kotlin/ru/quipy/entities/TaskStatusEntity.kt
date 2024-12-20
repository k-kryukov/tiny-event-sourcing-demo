package ru.quipy.entities

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class TaskStatusEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    var name: String = "",
    var projectID: UUID = UUID.randomUUID(),
    var color: Color = Color.LIGHT_BLUE,
    var priority: Int = 0,
)
