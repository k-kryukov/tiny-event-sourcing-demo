package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.create
import ru.quipy.api.UserCreatedEvent
import ru.quipy.api.UserInfoUpdatedEvent
import ru.quipy.logic.updateInfo
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {

    @PostMapping("/create")
    fun createUser(
        @RequestParam(required = true, value = "username") username: String,
        @RequestParam(required = true, value = "password") password: String) : UserCreatedEvent {
            return userEsService.create() {
                it.create(UUID.randomUUID(), username, password)
             }
    }

    @PostMapping("/update")
    fun updateUser(
        @RequestParam(required = true, value = "id") id: UUID,
        @RequestParam(required = true, value = "username") username: String,
        @RequestParam(required = true, value = "password") password: String) : UserInfoUpdatedEvent {
        return userEsService.update(id) {
            it.updateInfo(id, username, password)
        }
    }

    @GetMapping("/{userId}")
    fun getAccount(@PathVariable userId: UUID) : UserAggregateState? {
        return userEsService.getState(userId)
    }
}
