package ru.quipy.projections

import java.util.*

data class TaskProjectionDTO(
    val id: UUID = UUID.randomUUID(),
    var name: String = "",
    var description: String = "",
    val statusID: UUID = UUID.randomUUID()
)

fun TaskProjectionEntity.toDto() : TaskProjectionDTO {
    return TaskProjectionDTO(
        this.id,
        this.name,
        this.description,
        this.statusID
    )
}
