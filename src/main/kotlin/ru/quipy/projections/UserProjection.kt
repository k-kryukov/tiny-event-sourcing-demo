package ru.quipy.projections

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.entities.UserEntity
import ru.quipy.entities.UserRepository
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import java.util.*

@Service
@AggregateSubscriber(
    aggregateClass = UserAggregate::class, subscriberName = "user-projection",
)
class UserProjection(
    private val userRepository: UserRepository,
) {
    @SubscribeEvent
    fun userCreatedEventSubscriber(event: UserCreatedEvent) {
        userRepository.save(
            UserEntity(
                event.userID,
                event.login,
                event.name,
                event.password
            )
        )
    }

    @SubscribeEvent
    fun userUpdatedEvenSubscriber(event: UserCreatedEvent) {
        userRepository.save(
            UserEntity(
                event.userID,
                event.login,
                event.name,
                event.password
            )
        )
    }

    fun findUser(userID: UUID): UserEntity? {
        return userRepository.findByIdOrNull(userID)
    }

    fun listUsersByName(name: String): List<UserEntity> {
        return userRepository.findAllByName(name)
    }
}