package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForObject
import org.springframework.boot.test.web.server.LocalServerPort
import ru.quipy.controller.ProjectController
import ru.quipy.controller.UserController
import java.lang.reflect.InvocationTargetException
import java.util.*


@SpringBootTest
class DemoApplicationTests {
	@Autowired
	private lateinit var projectController: ProjectController

	@Autowired
	private lateinit var usersController: UserController

	@Test
	fun contextLoads() {
	}

	@Test
	fun createUser() {
		val userID = usersController.createUser("test", "123").userId

		val user = usersController.getAccount(userID)
		Assertions.assertEquals("test", user?.username)
	}

	@Test
	fun updateUser() {
		val userID = usersController.createUser("test", "123").userId
		usersController.updateUser(userID, "test2", "123")

		val user = usersController.getAccount(userID)
		Assertions.assertEquals("test2", user?.username)
	}

	@Test
	fun createProject() {
		val userID = usersController.createUser("test", "123").userId
		val projectID = projectController.createProject(userID, "my_project").projectId

		val project = projectController.getProject(projectID)
		Assertions.assertEquals("my_project", project?.projectName)
		Assertions.assertEquals(1, project?.projectMembers?.size)
		Assertions.assertEquals(userID, project?.projectMembers?.first())
	}

	@Test
	fun changeProject() {
		val userID = usersController.createUser("test", "123").userId
		val projectID = projectController.createProject(userID, "my_project").projectId

		var project = projectController.getProject(projectID)
		Assertions.assertEquals("my_project", project?.projectName)
		Assertions.assertEquals(1, project?.projectMembers?.size)
		Assertions.assertEquals(userID, project?.projectMembers?.first())

		projectController.updateProject(projectID, "my_project_2")
		project = projectController.getProject(projectID)
		Assertions.assertEquals("my_project_2", project?.projectName)
	}

	@Test
	fun createStatus() {
		val userID = usersController.createUser("test", "123").userId
		val projectID = projectController.createProject(userID, "my_project").projectId

		val statusID = projectController.createStatus(projectID, "task_status", "task_colour").statusId
		val taskID = UUID.randomUUID()
		val project = projectController.getProject(projectID)

		Assertions.assertEquals(project?.statuses?.keys?.contains(statusID), true)
	}

	@Test
	fun createProjectMember() {
		val userID = usersController.createUser("test", "123").userId
		val projectID = projectController.createProject(userID, "my_project").projectId

		val newMemberID = usersController.createUser("test2", "123").userId

		projectController.addProjectMember(projectID, newMemberID)

		val project = projectController.getProject(projectID)
		Assertions.assertEquals(mutableSetOf(userID, newMemberID), project?.projectMembers)
	}

	@Test
	fun createTask() {
		val userID = usersController.createUser("test", "123").userId
		val projectID = projectController.createProject(userID, "my_project").projectId

		projectController.createTask(projectID, "task_name", "some useful description")
		val project = projectController.getProject(projectID)
		Assertions.assertEquals(project?.tasks?.size, 1)
		Assertions.assertEquals(project?.tasks?.values?.first()?.name, "task_name")
	}

	@Test
	fun assignMemberToTask() {
		val userID = usersController.createUser("test", "123").userId
		val projectID = projectController.createProject(userID, "my_project").projectId
		val taskID = projectController.createTask(projectID, "task_name", "some useful description").taskId
		var project = projectController.getProject(projectID)
		Assertions.assertEquals(project?.tasks?.values?.first()?.assigneesAssigned, mutableSetOf<UUID>())

		projectController.addAssignee(projectID, taskID, userID)

		project = projectController.getProject(projectID)
		Assertions.assertEquals(project?.tasks?.values?.first()?.assigneesAssigned, mutableSetOf(userID))
	}

	@Test
	fun editTask() {
		val userID = usersController.createUser("test", "123").userId
		val projectID = projectController.createProject(userID, "my_project").projectId
		val taskID = projectController.createTask(projectID, "task_name", "some useful description").taskId
		var project = projectController.getProject(projectID)
		Assertions.assertEquals(project?.tasks?.values?.first()?.name, "task_name")

		projectController.editTask(projectID, taskID, "task_name_2", "new_desc")

		project = projectController.getProject(projectID)
		Assertions.assertEquals(project?.tasks?.values?.first()?.name, "task_name_2")
	}

	@Test
	fun assignStatus() {
		val userID = usersController.createUser("test", "123").userId
		val projectID = projectController.createProject(userID, "my_project").projectId
		val statusID = projectController.createStatus(projectID, "task_status", "task_colour").statusId
		val taskID = projectController.createTask(projectID, "task_name", "some useful description").taskId

		projectController.assignStatus(projectID, statusID, taskID)
		var project = projectController.getProject(projectID)

		Assertions.assertEquals(project?.tasks?.values?.first()?.statusAssigned, statusID)
	}

	@Test
	fun deleteStatusWhenAssigned() {
		val userID = usersController.createUser("test", "123").userId
		val projectID = projectController.createProject(userID, "my_project").projectId
		val statusID = projectController.createStatus(projectID, "task_status", "task_colour").statusId
		val taskID = projectController.createTask(projectID, "task_name", "some useful description").taskId

		projectController.assignStatus(projectID, statusID, taskID)
		var project = projectController.getProject(projectID)
		Assertions.assertEquals(project?.tasks?.values?.first()?.statusAssigned, statusID)

		try {
			projectController.deleteStatus(projectID, statusID)
			Assertions.fail("Exception is expected!")
		}
		catch (ex: InvocationTargetException) {
			Assertions.assertTrue(ex.targetException is RuntimeException)
		}
	}

	@Test
	fun changeStatusPriority() {
		val userID = usersController.createUser("test", "123").userId
		val projectID = projectController.createProject(userID, "my_project").projectId
		val statusID = projectController.createStatus(projectID, "task_status", "task_colour").statusId

		projectController.updateStatus(projectID, statusID, 25)

		var project = projectController.getProject(projectID)
		Assertions.assertEquals(25, project?.statuses?.values?.filter { it.id == statusID }?.first()?.priority)
	}
}
