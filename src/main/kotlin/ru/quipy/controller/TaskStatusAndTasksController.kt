package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.commands.*
import ru.quipy.core.EventSourcingService
import ru.quipy.entities.Color
import ru.quipy.entities.TaskEntity
import ru.quipy.entities.TaskStatusEntity
import ru.quipy.logic.TaskStatusAndTasksAggregateState
import java.util.*

@RestController
class TaskStatusAndTasksController(
    val taskEsService: EventSourcingService<UUID, TaskStatusAndTasksAggregate, TaskStatusAndTasksAggregateState>,
) {

    @PostMapping("/tasks/{taskAggregateID}/task/create")
    fun createTask(
        @PathVariable taskAggregateID: UUID,
        @RequestParam statusID: UUID,
        @RequestParam name: String,
        @RequestParam description: String,
    ): TaskCreatedEvent {
        return taskEsService.update(taskAggregateID) {
            it.createTask(UUID.randomUUID(), name, description, statusID)
        }
    }

    @PostMapping("/tasks/{taskAggregateID}/task/{taskID}")
    fun updateTask(
        @PathVariable taskAggregateID: UUID,
        @PathVariable taskID: UUID,
        @RequestParam name: String,
        @RequestParam description: String,
    ): TaskUpdatedEvent {
        return taskEsService.update(taskAggregateID) {
            it.updateTask(taskID, name, description)
        }
    }

    @PostMapping("/tasks/{taskAggregateID}/task/{taskID}/change-status")
    fun changeStatusForTask(
        @PathVariable taskAggregateID: UUID,
        @PathVariable taskID: UUID,
        @RequestParam statusID: UUID,
    ): StatusChangedForTaskEvent {
        return taskEsService.update(taskAggregateID) {
            it.changeStatusForTask(taskID, statusID)
        }
    }

    @DeleteMapping("/tasks/{taskAggregateID}/status/{statusID}")
    fun deleteTaskStatus(
        @PathVariable taskAggregateID: UUID,
        @PathVariable statusID: UUID,
    ): StatusDeletedEvent {
        return taskEsService.update(taskAggregateID) {
            it.deleteTaskStatus(statusID)
        }
    }

    @PostMapping("/tasks/{taskAggregateID}/status/{statusID}/change-priority")
    fun changeTaskStatusPriority(
        @PathVariable taskAggregateID: UUID,
        @PathVariable statusID: UUID,
        @RequestParam priority: Int,
    ): StatusPriorityChangedEvent {
        return taskEsService.update(taskAggregateID) {
            it.changeTaskStatusPriority(statusID, priority)
        }
    }

    @GetMapping("/tasks/{taskAggregateID}/task/{taskID}")
    fun getTask(@PathVariable taskAggregateID: UUID, @PathVariable taskID: UUID): TaskEntity? {
        return taskEsService.getState(taskAggregateID)?.getTaskByID(taskID)
    }

    @GetMapping("/tasks/{taskAggregateID}/task-statuses-and-tasks")
    fun getTaskStatusesAndTasks(@PathVariable taskAggregateID: UUID): TaskStatusAndTasksAggregateState? {
        return taskEsService.getState(taskAggregateID)
    }

    @PostMapping("/tasks/{taskAggregateID}/task-status/create")
    fun createTaskStatus(
        @PathVariable taskAggregateID: UUID,
        @RequestParam name: String,
        @RequestParam color: String,
        @RequestParam projectID: UUID
    ): TaskStatusCreatedEvent {
        return taskEsService.update(taskAggregateID) {
            it.createTaskStatus(
                UUID.randomUUID(),
                name,
                taskAggregateID,
                Color.valueOf(color),
                projectID
            )
        }
    }

    @GetMapping("/tasks/{taskAggregateID}/task-status/{id}")
    fun getTaskStatus(@PathVariable taskAggregateID: UUID, @PathVariable id: UUID): TaskStatusEntity? {
        return taskEsService.getState(taskAggregateID)?.getStatusByID(id)
    }

    @PostMapping("/tasks/{taskAggregateID}/task/{taskID}/add-assignee")
    fun addAssigneeForTask(
        @PathVariable taskAggregateID: UUID,
        @PathVariable taskID: UUID,
        @RequestParam memberID: UUID,
    ): TaskAssigneeAddedEvent? {
        return taskEsService.update(taskAggregateID) {
            it.addTaskAssignee(taskID, memberID)
        }
    }
}
