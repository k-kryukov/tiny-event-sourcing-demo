package ru.quipy

import java.util.UUID
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.quipy.api.UserCreatedEvent
import ru.quipy.controller.UserController
import ru.quipy.controller.ProjectAndProjectMembersController

@SpringBootTest
class ProjectAndProjectMembersTests {

	@Autowired
	private lateinit var userController: UserController

	@Autowired
	private lateinit var projectController: ProjectAndProjectMembersController

	@Test
	fun manipulateProject() {
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
		val project = projectController.createProject(
			"testProject",
			owner.userID
		)
		Assertions.assertEquals(1, project.version)
		Assertions.assertEquals("testProject", project.projectName)

		val response = projectController.getProject(project.projectID)
		Assertions.assertNotNull(response)

		val ownerInProject = response!!.getProjectMemberByID(response.getProjectMembers()[0].id)
		Assertions.assertNotNull(ownerInProject)
		Assertions.assertEquals("testProject", response.getName())
		Assertions.assertEquals("Owner", ownerInProject!!.name)
		Assertions.assertEquals("Owner", ownerInProject.login)
		Assertions.assertEquals(response.getId(), ownerInProject.projectID)

		val newProjectMember = projectController.createProjectMember(
			response.getId(),
			user.userID
		)
		Assertions.assertNotNull(ownerInProject)
		Assertions.assertEquals("ProjectMember", newProjectMember.memberLogin)
		Assertions.assertEquals("ProjectMember", newProjectMember.memberName)
		Assertions.assertEquals(user.userID, newProjectMember.userID)
		Assertions.assertEquals(response.getId(), newProjectMember.projectID)

		val memberResponse = projectController.getProjectMember(
			response.getId(),
			newProjectMember.memberID
		)
		Assertions.assertNotNull(memberResponse)
		Assertions.assertEquals("ProjectMember", memberResponse!!.login)
		Assertions.assertEquals("ProjectMember", memberResponse.name)
		Assertions.assertEquals(user.userID, memberResponse.userID)
		Assertions.assertEquals(response.getId(), memberResponse.projectID)

		val memberNullResponse = projectController.getProjectMember(
			response.getId(),
			UUID.randomUUID()
		)
		Assertions.assertNull(memberNullResponse)
	}

	@Test
	fun editProject() {
		val owner = userController.createUser(
			"Owner",
			"Owner",
			"testPassword"
		)
		val project = projectController.createProject(
			"testProject",
			owner.userID
		)
		Assertions.assertEquals(1, project.version)
		Assertions.assertEquals("testProject", project.projectName)

		projectController.updateProject(project.projectID, "testProject1")
		val response = projectController.getProject(project.projectID)
		Assertions.assertEquals("testProject1", response?.getName())
	}
}
