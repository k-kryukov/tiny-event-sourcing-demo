package ru.quipy.logic

import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.api.UserUpdatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.UUID
import ru.quipy.entities.UserEntity

class UserAggregateState : AggregateState<UUID, UserAggregate> {
    private lateinit var user: UserEntity

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    override fun getId() = user.id

    fun getLogin() = user.login

    fun getName() = user.name

    fun getPassword() = user.password

    @StateTransitionFunc
    fun userCreatedApply(event: UserCreatedEvent) {
        user = UserEntity(
            id = event.userID,
            login = event.login,
            name = event.userName,
            password = event.password
        )

        createdAt = event.createdAt
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun userUpdatedApply(event: UserUpdatedEvent) {
        user = UserEntity(
            id = event.userID,
            login = event.login,
            name = event.userName,
            password = event.password
        )

        updatedAt = event.createdAt
    }
}
