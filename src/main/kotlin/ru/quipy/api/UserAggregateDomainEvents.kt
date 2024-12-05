package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val USER_REGISTERED_EVENT = "USER_REGISTERED_EVENT"
const val USER_UPDATE_INFO_EVENT = "USER_UPDATE_INFO_EVENT"

@DomainEvent(name = USER_REGISTERED_EVENT)
class UserCreatedEvent(
    val userId: UUID,
    val username: String,
    val password: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<UserAggregate>(
    name = USER_REGISTERED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = USER_UPDATE_INFO_EVENT)
class UserInfoUpdatedEvent(
    val userId: UUID,
    val username: String,
    val password: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<UserAggregate>(
    name = USER_UPDATE_INFO_EVENT,
    createdAt = createdAt,
)
