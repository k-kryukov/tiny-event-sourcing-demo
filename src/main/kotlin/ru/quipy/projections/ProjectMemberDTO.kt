package ru.quipy.projections

import ru.quipy.entities.ProjectMemberEntity
import java.util.*

data class ProjectMemberDTO(
    val id: UUID = UUID.randomUUID(),
    val name: String = "",
    val login: String = "",
    val userId: UUID = UUID.randomUUID(),
)

fun ProjectMemberEntity.toDto() : ProjectMemberDTO {
    return ProjectMemberDTO(
        this.id,
        this.name,
        this.login,
        this.userID
    )
}
