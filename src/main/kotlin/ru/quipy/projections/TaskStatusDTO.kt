package ru.quipy.projections

import ru.quipy.entities.Color
import ru.quipy.entities.TaskStatusEntity
import java.util.*

data class TaskStatusDTO(
    val id: UUID = UUID.randomUUID(),
    val name: String = "",
    val color: Color = Color.LIGHT_BLUE,
    val tasks: List<TaskProjectionDTO> = mutableListOf(),
    var priority: Int = 0,
)

fun TaskStatusEntity.toDto(tasks: List<TaskProjectionDTO>) : TaskStatusDTO {
    return TaskStatusDTO(
        this.id,
        this.name,
        this.color,
        tasks,
        this.priority
    )
}
