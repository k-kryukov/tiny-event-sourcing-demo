package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.api.UserCreatedEvent
import ru.quipy.controller.ProjectAndProjectMembersController
import ru.quipy.controller.TaskStatusAndTasksController
import ru.quipy.controller.UserController
import ru.quipy.entities.TaskStatusEntity
import ru.quipy.entities.Color
import ru.quipy.logic.ProjectAndProjectMembersAggregateState
import java.util.UUID

val zeroStatus = TaskStatusEntity(
	name = "CREATED",
	projectID = UUID.randomUUID(),
	color = Color.GREEN,
	priority = 0,
)
val firstStatus = TaskStatusEntity(
	name = "1",
	projectID = UUID.randomUUID(),
	color = Color.ORANGE,
	priority = 0,
)
val secondStatus = TaskStatusEntity(
	name = "2",
	projectID = UUID.randomUUID(),
	color = Color.BLUE,
	priority = 0,
)
val thirdStatus = TaskStatusEntity(
	name = "3",
	projectID = UUID.randomUUID(),
	color = Color.RED,
	priority = 0,
)

@SpringBootTest
class TasksAndStatusesTests {

	@Autowired
	private lateinit var userController: UserController

	@Autowired
	private lateinit var projectController: ProjectAndProjectMembersController

	@Autowired
	private lateinit var taskController: TaskStatusAndTasksController

	@Test
	fun tasksAndStatuses() {
		val owner = createUser("Owner")
		val user = createUser("ProjectMember")

		val project = createProjectWithProjectMember(owner.userID, user.userID)

		Thread.sleep(500)

		val taskAggregate = taskController.getTaskStatusesAndTasks(project!!.getStatusesAndTasksAggregateId())

		Assertions.assertNotNull(taskAggregate)
		Assertions.assertEquals(1, taskAggregate!!.getStatuses().size)
		Assertions.assertEquals("CREATED", taskAggregate.getStatuses()[0].name)
		Assertions.assertEquals(Color.GREEN, taskAggregate.getStatuses()[0].color)
		Assertions.assertEquals(1, taskAggregate.getStatuses()[0].priority)
		Assertions.assertEquals(0, taskAggregate.getTasks().size)

		val defStatus = taskController.getTaskStatus(taskAggregate.getId(), taskAggregate.getStatuses()[0].id)
		Assertions.assertNotNull(defStatus)
		Assertions.assertEquals("CREATED", defStatus!!.name)
		Assertions.assertEquals(Color.GREEN, defStatus.color)
		Assertions.assertEquals(1, defStatus.priority)

		val newStatus = taskController.createTaskStatus(
			taskAggregate.getId(),
			"Completed",
			"ORANGE",
			null
		)
		val newStatusAgg = taskController.getTaskStatus(taskAggregate.getId(), newStatus.statusID)
		Assertions.assertNotNull(newStatusAgg)
		Assertions.assertEquals("Completed", newStatusAgg!!.name)
		Assertions.assertEquals(Color.ORANGE, newStatusAgg.color)
		Assertions.assertEquals(2, newStatusAgg.priority)

		val task = taskController.createTask(
			taskAggregate.getId(),
			newStatus.statusID,
			"Task",
			"Description1"
		)
		val taskAgg = taskController.getTask(taskAggregate.getId(), task.taskID)
		Assertions.assertNotNull(taskAgg)
		Assertions.assertEquals("Task", taskAgg!!.name)
		Assertions.assertEquals("Description1", taskAgg.description)
		Assertions.assertEquals(0, taskAgg.assignees.size)
		Assertions.assertEquals(newStatus.statusID, taskAgg.statusID)
		Assertions.assertEquals(project.getId(), taskAgg.projectID)

		Assertions.assertThrows(
			IllegalStateException::class.java
		) {
			taskController.deleteTaskStatus(taskAggregate.getId(), newStatus.statusID)
		}

		val taskNewStatus = taskController.changeStatusForTask(
			taskAggregate.getId(),
			task.taskID,
			defStatus.id
		)
		val taskAggNewStatus = taskController.getTask(taskAggregate.getId(), taskNewStatus.taskID)
		Assertions.assertNotNull(taskAggNewStatus)
		Assertions.assertEquals("Task", taskAggNewStatus!!.name)
		Assertions.assertEquals(defStatus.id, taskAggNewStatus.statusID)

		val taskNewNameDescription = taskController.updateTask(
			taskAggregate.getId(),
			taskAgg.id,
			"newNameTask",
			"newDescription"
		)
		val taskAggNewNameDescription = taskController.getTask(taskAggregate.getId(), taskNewNameDescription.taskID)
		Assertions.assertNotNull(taskAggNewNameDescription)
		Assertions.assertEquals("newNameTask", taskAggNewNameDescription!!.name)
		Assertions.assertEquals("newDescription", taskAggNewNameDescription.description)
		Assertions.assertEquals(defStatus.id, taskAggNewStatus.statusID)

		taskController.deleteTaskStatus(taskAggregate.getId(), newStatus.statusID)
		val agg = taskController.getTaskStatusesAndTasks(taskAggregate.getId())
		Assertions.assertNotNull(agg)
		Assertions.assertEquals(1, agg!!.getStatuses().size)
		Assertions.assertEquals("CREATED", agg.getStatuses()[0].name)
		Assertions.assertEquals(Color.GREEN, agg.getStatuses()[0].color)
		Assertions.assertEquals(1, agg.getStatuses()[0].priority)
		Assertions.assertEquals(1, agg.getTasks().size)
	}


	@Test
	fun statusesSwapPriorities() {
		val owner = createUser("Owner")
		val user = createUser("ProjectMember")
		val project = createProjectWithProjectMember(owner.userID, user.userID)

		val zeroID = taskController
			.getTaskStatusesAndTasks(project!!.getStatusesAndTasksAggregateId())!!
			.getStatuses()[0].id

		val firstID = createStatus(
			project.getStatusesAndTasksAggregateId(),
			firstStatus
		).statusID
		val secondID = createStatus(
			project.getStatusesAndTasksAggregateId(),
			secondStatus
		).statusID
		val thirdID = createStatus(
			project.getStatusesAndTasksAggregateId(),
			thirdStatus
		).statusID

		val taskAggregate = taskController.getTaskStatusesAndTasks(project.getStatusesAndTasksAggregateId())
		Assertions.assertEquals(4, taskAggregate!!.getStatuses().size)
		validateStatus(taskAggregate.getId(), zeroID, zeroStatus, 1)
		validateStatus(taskAggregate.getId(), firstID, firstStatus, 2)
		validateStatus(taskAggregate.getId(), secondID, secondStatus, 3)
		validateStatus(taskAggregate.getId(), thirdID, thirdStatus, 4)

		taskController.changeTaskStatusPriority(
			taskAggregate.getId(),
			firstID,
			4
		)
		validateStatus(taskAggregate.getId(), zeroID, zeroStatus, 1)
		validateStatus(taskAggregate.getId(), firstID, firstStatus, 4)
		validateStatus(taskAggregate.getId(), secondID, secondStatus, 2)
		validateStatus(taskAggregate.getId(), thirdID, thirdStatus, 3)

		taskController.changeTaskStatusPriority(
			taskAggregate.getId(),
			thirdID,
			1
		)
		validateStatus(taskAggregate.getId(), zeroID, zeroStatus, 2)
		validateStatus(taskAggregate.getId(), firstID, firstStatus, 4)
		validateStatus(taskAggregate.getId(), secondID, secondStatus, 3)
		validateStatus(taskAggregate.getId(), thirdID, thirdStatus, 1)

		taskController.deleteTaskStatus(taskAggregate.getId(), thirdID)
		validateStatus(taskAggregate.getId(), zeroID, zeroStatus, 1)
		validateStatus(taskAggregate.getId(), firstID, firstStatus, 3)
		validateStatus(taskAggregate.getId(), secondID, secondStatus, 2)
	}

	private fun validateStatus(aggID: UUID, actualID: UUID, expectedStatus: TaskStatusEntity, expectedPriority: Int) {
		val status = taskController.getTaskStatus(aggID, actualID)
		Assertions.assertEquals(expectedStatus.color, status!!.color)
		Assertions.assertEquals(expectedStatus.name, status.name)
		Assertions.assertEquals(expectedPriority, status.priority)
	}

	private fun createStatus(aggregateID: UUID, status: TaskStatusEntity) : TaskStatusCreatedEvent {
		return taskController.createTaskStatus(
			aggregateID,
			status.name,
			status.color.name,
			null
		)
	}

	private fun createUser(name: String) : UserCreatedEvent {
		return userController.createUser(
			"Login$name",
			name,
			"testPassword"
		)
	}

	private fun createProjectWithProjectMember(ownerID: UUID, userID: UUID): ProjectAndProjectMembersAggregateState? {
		val projectID = projectController.createProject(
			"testProject",
			ownerID
		).projectID
		projectController.createProjectMember(
			projectID,
			userID
		)

		return projectController.getProject(projectID)
	}
}
