package ru.quipy.projections

import ru.quipy.entities.ProjectEntity
import java.util.*

data class ProjectDTO(
    val id: UUID = UUID.randomUUID(),
    val name: String = "",
    val members: List<ProjectMemberDTO> = mutableListOf(),
    val taskStatuses: List<TaskStatusDTO> = mutableListOf(),
)

fun ProjectEntity.toDto(members: List<ProjectMemberDTO>, statuses: List<TaskStatusDTO>): ProjectDTO {
    return ProjectDTO(
        this.id,
        this.name,
        members,
        statuses
    )
}