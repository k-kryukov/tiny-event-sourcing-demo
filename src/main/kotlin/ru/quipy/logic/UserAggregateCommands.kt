package ru.quipy.logic

import ru.quipy.api.UserInfoUpdatedEvent
import ru.quipy.api.UserCreatedEvent
import java.util.*


fun UserAggregateState.create(
    userId: UUID,
    username: String,
    password: String): UserCreatedEvent {
        return UserCreatedEvent(
            userId = userId,
            username = username,
            password = password,
        )
}

fun UserAggregateState.updateInfo(
    userId: UUID,
    username: String,
    password: String): UserInfoUpdatedEvent {
    return UserInfoUpdatedEvent(
        userId = userId,
        username = username,
        password = password,
    )
}
