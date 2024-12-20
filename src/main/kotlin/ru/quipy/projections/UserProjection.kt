package ru.quipy.projections

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.entities.UserEntity
import ru.quipy.entities.UserRepository
import ru.quipy.logic.UserAggregateState
import ru.quipy.streams.AggregateSubscriptionsManager
import ru.quipy.streams.annotation.AggregateSubscriber
import java.util.*
import javax.annotation.PostConstruct

@Service
@AggregateSubscriber(
    aggregateClass = UserAggregate::class, subscriberName = "user-projection"
)
class UserProjection(
    private val userRepository: UserRepository,
    private val subManager: AggregateSubscriptionsManager,
    private val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {

    @PostConstruct
    fun init() {
        subManager.createSubscriber(UserAggregate::class, "user:user-projection") {
            `when`(UserCreatedEvent::class) { event ->
                withContext(Dispatchers.IO) {
                    userRepository.save(
                        UserEntity(
                            event.userID,
                            event.login,
                            event.userName,
                            event.password
                        )
                    )
                }
            }
            `when`(UserUpdatedEvent::class) { event ->
                withContext(Dispatchers.IO) {
                    userRepository.save(
                        UserEntity(
                            event.userID,
                            event.login,
                            event.userName,
                            event.password
                        )
                    )
                }
            }
        }
    }

    fun findUser(userID: UUID): UserEntity? {
        return userRepository.findByIdOrNull(userID)
    }

    fun resolveUsersByName(name: String): List<UserEntity> {
        return userRepository.findAllByName(name)
    }
}