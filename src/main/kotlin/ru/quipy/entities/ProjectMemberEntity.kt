package ru.quipy.entities

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class ProjectMemberEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    var name: String = "",
    var login: String = "",
    var userID: UUID = UUID.randomUUID(),
    var projectID: UUID = UUID.randomUUID(),
)
