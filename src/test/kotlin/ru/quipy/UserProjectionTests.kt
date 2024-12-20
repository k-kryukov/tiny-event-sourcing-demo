package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.quipy.api.UserCreatedEvent
import ru.quipy.controller.UserController
import ru.quipy.projections.ProjectionService
import java.lang.Thread.sleep
import java.util.*


@SpringBootTest
class UserProjectionTests {
    @Autowired
    private lateinit var userController: UserController

    @Autowired
    private lateinit var projectionService: ProjectionService

    @Test
    fun findUser() {
        val name = UUID.randomUUID().toString()
        val event = createUser(name)

        sleep(5000)  // Propagation delay

        val user = projectionService.findUser(event.userID)
        Assertions.assertNotNull(user!!)
        Assertions.assertEquals(user.id, event.userID)
        Assertions.assertEquals(user.name, event.userName)
        Assertions.assertEquals(user.login, event.login)
    }

    @Test
    fun resolveUsersByName() {
        val name = UUID.randomUUID().toString()
        val userCreatedEvent1 = createUser(name)
        val userCreatedEvent2 = createUser(name)
        val userCreatedEvent3 = createUser(UUID.randomUUID().toString())

        sleep(5000)  // Propagation delay

        val users = projectionService.resolveUsersByName(name)
        Assertions.assertEquals(users.size, 2)
        Assertions.assertTrue(users.any { user -> user.id == userCreatedEvent1.userID })
        Assertions.assertTrue(users.any { user -> user.id == userCreatedEvent2.userID })
        Assertions.assertFalse(users.any { user -> user.id == userCreatedEvent3.userID })
    }

    private fun createUser(name: String): UserCreatedEvent {
        return userController.createUser(
            "Login$name",
            name,
            "testPassword"
        )
    }
}