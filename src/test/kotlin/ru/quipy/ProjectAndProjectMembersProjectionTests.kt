package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.quipy.controller.ProjectAndProjectMembersController
import ru.quipy.controller.UserController
import ru.quipy.projections.ProjectionService
import java.lang.Thread.sleep

@SpringBootTest
class ProjectAndProjectMembersProjectionTests {
    @Autowired
    private lateinit var userController: UserController

    @Autowired
    private lateinit var projectController: ProjectAndProjectMembersController

    @Autowired
    private lateinit var projectionService: ProjectionService

    @Test
    fun findProjectTest() {
        val owner = userController.createUser(
            "Owner",
            "Owner",
            "testPassword"
        )
        val projectCreatedEvent = projectController.createProject(
            "testProject",
            owner.userID
        )

        sleep(5000)  // Propagation delay

        val project = projectionService.findProject(projectCreatedEvent.projectID)
        Assertions.assertNotNull(project!!)
        Assertions.assertEquals(project.id, projectCreatedEvent.projectID)
        Assertions.assertEquals(project.name, projectCreatedEvent.projectName)
    }

    @Test
    fun resolveProjectmembersByproject() {
        val owner = userController.createUser(
            "Owner",
            "Owner",
            "testPassword"
        )
        val user = userController.createUser(
            "ProjectMember",
            "ProjectMember",
            "testPassword"
        )
        val projectCreatedEvent = projectController.createProject(
            "testProject",
            owner.userID
        )
        projectController.createProjectMember(
            projectCreatedEvent.projectID,
            user.userID
        )

        sleep(5000)  // Propagation delay

        val project = projectionService.findProject(projectCreatedEvent.projectID)
        Assertions.assertNotNull(project!!)
        Assertions.assertEquals(project.id, projectCreatedEvent.projectID)
        Assertions.assertEquals(project.name, projectCreatedEvent.projectName)
        Assertions.assertEquals(project.members.size, 2)
        Assertions.assertTrue(project.members.any { member -> member.userId == user.userID })
        Assertions.assertTrue(project.members.any { member -> member.userId == owner.userID })
    }

    @Test
    fun resolveProjectsByUser() {
        val owner = userController.createUser(
            "Owner",
            "Owner",
            "testPassword"
        )
        val projectCreatedEvent1 = projectController.createProject(
            "testProject1",
            owner.userID
        )
        val projectCreatedEvent2 = projectController.createProject(
            "testProject2",
            owner.userID
        )

        sleep(5000)  // Propagation delay

        val projects = projectionService.resolveProjectsByUser(owner.userID)
        Assertions.assertEquals(projects.size, 2)
        Assertions.assertTrue(projects.any { project -> project.id == projectCreatedEvent1.projectID })
        Assertions.assertTrue(projects.any { project -> project.id == projectCreatedEvent2.projectID })
    }
}