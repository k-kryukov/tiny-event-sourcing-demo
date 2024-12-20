package ru.quipy.projections

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class TaskProjectionEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    var name: String = "",
    var description: String = "",
    var statusID: UUID = UUID.randomUUID(),
)