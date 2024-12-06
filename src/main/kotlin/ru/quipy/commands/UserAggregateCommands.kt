package ru.quipy.commands

import ru.quipy.api.UserCreatedEvent
import ru.quipy.api.UserUpdatedEvent
import ru.quipy.logic.UserAggregateState
import java.util.*

fun UserAggregateState.createUser(id: UUID, login: String, name: String, password: String): UserCreatedEvent {
    return UserCreatedEvent(
        userID = id,
        login = login,
        userName = name,
        password = password
    )
}

fun UserAggregateState.updateUser(id: UUID, login: String, name: String, password: String): UserUpdatedEvent {
    return UserUpdatedEvent(
        userID = id,
        login = login,
        userName = name,
        password = password
    )
}
