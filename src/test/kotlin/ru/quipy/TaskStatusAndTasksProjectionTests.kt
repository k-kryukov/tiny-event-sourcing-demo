package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.quipy.controller.ProjectAndProjectMembersController
import ru.quipy.controller.TaskStatusAndTasksController
import ru.quipy.controller.UserController
import ru.quipy.logic.ProjectAndProjectMembersAggregateState
import ru.quipy.projections.ProjectionService
import java.lang.Thread.sleep
import java.util.*

@SpringBootTest
class TaskStatusAndTasksProjectionTests {
    @Autowired
    private lateinit var userController: UserController

    @Autowired
    private lateinit var projectController: ProjectAndProjectMembersController

    @Autowired
    private lateinit var taskController: TaskStatusAndTasksController

    @Autowired
    private lateinit var projectionService: ProjectionService

    @Test
    fun listStatusesByProject() {
        val owner = userController.createUser(
            "Owner",
            "Owner",
            "testPassword"
        )
        val projectAggregate = createProject(owner.userID)
        val taskAggregate = taskController.getTaskStatusesAndTasks(projectAggregate!!.getStatusesAndTasksAggregateId())

        val statusCreatedEvent = taskController.createTaskStatus(
            taskAggregate!!.getId(),
            "Completed",
            "ORANGE",
            projectAggregate.getId()
        )

        sleep(5000) // Propagation delay

        val project = projectionService.findProject(projectAggregate.getId())
        Assertions.assertNotNull(project!!)
        Assertions.assertEquals(project.taskStatuses.size, 2)
        Assertions.assertTrue(project.taskStatuses.any { taskStatus ->
            taskStatus.id == statusCreatedEvent.statusID &&
                    taskStatus.name == statusCreatedEvent.statusName &&
                    taskStatus.color == statusCreatedEvent.color
        })

        taskController.deleteTaskStatus(
            taskAggregate.getId(),
            statusCreatedEvent.statusID
        )
        sleep(5000) // Propagation delay

        val updateProject = projectionService.findProject(projectAggregate.getId())
        Assertions.assertNotNull(updateProject!!)
        Assertions.assertEquals(updateProject.taskStatuses.size, 1)
    }

    @Test
    fun listTasksByProjectByStatus() {
        val owner = userController.createUser(
            "Owner",
            "Owner",
            "testPassword"
        )
        val projectAggregate = createProject(owner.userID)
        val taskAggregate = taskController.getTaskStatusesAndTasks(projectAggregate!!.getStatusesAndTasksAggregateId())

        val statusCreatedEvent1 = taskController.createTaskStatus(
            taskAggregate!!.getId(),
            "Completed",
            "ORANGE",
            projectAggregate.getId()
        )
        val statusCreatedEvent2 = taskController.createTaskStatus(
            taskAggregate!!.getId(),
            "WontFix",
            "BLUE",
            projectAggregate.getId()
        )

        val task1 = taskController.createTask(
            taskAggregate.getId(),
            statusCreatedEvent1.statusID,
            "Task1",
            "Description1"
        )

        val task2 = taskController.createTask(
            taskAggregate.getId(),
            statusCreatedEvent2.statusID,
            "Task2",
            "Description2"
        )

        sleep(5000)  // Propagation delay

        val tasksByProject = projectionService.resolveTasksByProject(projectAggregate.getId())
        Assertions.assertEquals(tasksByProject.size, 2)
        Assertions.assertTrue(tasksByProject.any { task -> task.id == task1.taskID })
        Assertions.assertTrue(tasksByProject.any { task -> task.id == task2.taskID })

        val tasksByStatus = projectionService.resolveTasksByStatus(statusCreatedEvent1.statusID)
        Assertions.assertEquals(tasksByStatus.size, 1)
        Assertions.assertTrue(tasksByProject.any { task -> task.id == task1.taskID })

        val taskProjection = projectionService.findTask(task1.taskID)
        Assertions.assertEquals(taskProjection!!.id, task1.taskID)
        Assertions.assertEquals(taskProjection.name, task1.taskName)
        Assertions.assertEquals(taskProjection.statusID, task1.statusID)
        Assertions.assertEquals(taskProjection.description, task1.description)

        taskController.changeStatusForTask(
            taskAggregate.getId(),
            task1.taskID,
            statusCreatedEvent2.statusID,
        )
        val updatedTask = taskController.updateTask(
            taskAggregate.getId(),
            task1.taskID,
            "Task1-update",
            "Description1-update"
        )

        sleep(5000) // Propagation delay

        val updatedTaskProjection = projectionService.findTask(task1.taskID)
        Assertions.assertEquals(updatedTaskProjection!!.id, updatedTask.taskID)
        Assertions.assertEquals(updatedTaskProjection.name, updatedTask.taskName)
        Assertions.assertEquals(updatedTaskProjection.statusID, statusCreatedEvent2.statusID)
        Assertions.assertEquals(updatedTaskProjection.description, updatedTask.description)
    }

    private fun createProject(ownerID: UUID): ProjectAndProjectMembersAggregateState? {
        val projectID = projectController.createProject(
            "testProject",
            ownerID
        ).projectID

        return projectController.getProject(projectID)
    }
}