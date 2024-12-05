package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
) {
    @PostMapping("create")
    fun createProject(
        @RequestParam(required = true, value = "userID") userID: UUID,
        @RequestParam(required = true, value = "projectName") projectName: String,
    ) : ProjectCreatedEvent {
        return projectEsService.create {
            it.create(UUID.randomUUID(), projectName, userID)
        }
    }

    @PostMapping("update")
    fun updateProject(
        @RequestParam(required = true, value = "projectID") projectID: UUID,
        @RequestParam(required = true, value = "projectName") projectName: String,
    ) : ProjectUpdatedEvent {
        return projectEsService.update(projectID) {
            it.update(projectName)
        }
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID) : ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PostMapping("/{projectId}/tasks/create")
    fun createTask(@PathVariable projectId: UUID,
                   @RequestParam(required = true, value = "taskName") taskName: String,
                   @RequestParam(required = true, value = "description") description: String) : TaskCreatedEvent {
        return projectEsService.update(projectId) {
            it.addTask(taskName, description)
        }
    }

    @PostMapping("/{projectId}/tasks/edit")
    fun editTask(@PathVariable projectId: UUID,
                 @PathVariable taskId: UUID,
                   @RequestParam(required = true, value = "taskName") taskName: String,
                   @RequestParam(required = true, value = "description") description: String) : TaskEditedEvent {
        return projectEsService.update(projectId) {
            it.editTask(taskId, taskName, description)
        }
    }

    @PostMapping("/{projectId}/statuses/create")
    fun createStatus(
        @PathVariable projectId: UUID,
        @RequestParam(required = true, value = "statusName") statusName: String,
        @RequestParam(required = true, value = "statusColour") statusColour: String
    ) : StatusCreatedEvent {
        return projectEsService.update(projectId) {
            it.createStatus(name = statusName, colour = statusColour)
        }
    }

    @PostMapping("/{projectId}/statuses/assign")
    fun assignStatus(
        @PathVariable projectId: UUID,
        @RequestParam(required = true, value = "statusId") statusId: UUID,
        @RequestParam(required = true, value = "taskId") taskId: UUID) : StatusAssignedToTaskEvent {
        return projectEsService.update(projectId) {
            it.assignStatusToTask(statusId = statusId, taskId = taskId)
        }
    }

    @PostMapping("/{projectId}/statuses/update-priority")
    fun updateStatus(
        @PathVariable projectId: UUID,
        @RequestParam(required = true, value = "statusId") statusId: UUID,
        @RequestParam(required = true, value = "priority") priority: Int) : StatusPriorityUpdatedEvent {
        return projectEsService.update(projectId) {
            it.updateStatusPriority(statusId = statusId, priority = priority)
        }
    }

    @PostMapping("/{projectId}/statuses/delete")
    fun deleteStatus(
        @PathVariable projectId: UUID,
        @RequestParam(required = true, value = "statusId") statusId: UUID,
    ) : StatusDeletedEvent {
        return projectEsService.update(projectId) {
            it.deleteStatus(statusId = statusId)
        }
    }


    @PostMapping("/{projectId}/projectMembers/add")
    fun addProjectMember(
        @PathVariable projectId: UUID,
        @RequestParam(required = true, value = "userID") userID: UUID) : ProjectMemberAddedEvent {
        return projectEsService.update(projectId) {
            it.addProjectMember(userID = userID)
        }
    }

    @PostMapping("/{projectId}/task/assignees/add")
    fun addAssignee(
        @PathVariable projectId: UUID,
        @RequestParam(required = true, value = "taskId") taskId: UUID,
        @RequestParam(required = true, value = "projectMemberId") projectMemberId: UUID) : AssigneeAddedToTaskEvent {
        return projectEsService.update(projectId) {
            it.addAssigneeToTask(taskId = taskId, projectMemberId = projectMemberId)
        }
    }
}