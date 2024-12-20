package ru.quipy.projections

import ru.quipy.entities.UserEntity
import java.util.UUID

data class UserDTO(
    val id: UUID = UUID.randomUUID(),
    var name: String = "",
    var login: String = ""
)

fun UserEntity.toDto(): UserDTO {
    return UserDTO(
        this.id,
        this.name,
        this.login
    )
}