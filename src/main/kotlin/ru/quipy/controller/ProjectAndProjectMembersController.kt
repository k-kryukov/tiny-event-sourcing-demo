package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.entities.ProjectMemberEntity
import ru.quipy.entities.Color
import ru.quipy.logic.ProjectAndProjectMembersAggregateState
import ru.quipy.logic.TaskStatusAndTasksAggregateState
import ru.quipy.logic.UserAggregateState
import java.util.UUID
import ru.quipy.commands.*

@RestController
@RequestMapping("/project")
class ProjectAndProjectMembersController(
    val projectEsService: EventSourcingService<UUID, ProjectAndProjectMembersAggregate, ProjectAndProjectMembersAggregateState>,
    val taskEsService: EventSourcingService<UUID, TaskStatusAndTasksAggregate, TaskStatusAndTasksAggregateState>,
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {

    @PostMapping("/{projectID}/create-project-member")
    fun createProjectMember(@PathVariable projectID: UUID, @RequestParam userID: UUID) : ProjectMemberCreatedEvent {
        val user = userEsService.getState(userID)

        return projectEsService.update(projectID) {
            it.createProjectMember(UUID.randomUUID(), user?.getLogin(), user?.getName(), user?.getId(), projectID)
        }
    }

    @GetMapping("/{projectID}/member/{memberID}")
    fun getProjectMember(@PathVariable projectID: UUID, @PathVariable memberID: UUID) : ProjectMemberEntity? {
        return projectEsService.getState(projectID)?.getProjectMemberByID(memberID)
    }

    @PostMapping("/create")
    fun createProject(
        @RequestParam name: String,
        @RequestParam creatorID: UUID,
    ) : ProjectCreatedEvent {
        val user = userEsService.getState(creatorID)

        val response = projectEsService.create { it.createProject(UUID.randomUUID(), name) }

        val taskResponse = taskEsService.create {
            it.createTaskStatus(UUID.randomUUID(), "CREATED", UUID.randomUUID(), Color.GREEN, response.projectID)
        }
        projectEsService.update(response.projectID) {
            it.createProjectMember(UUID.randomUUID(), user?.getLogin(), user?.getName(), user?.getId(), response.projectID)
        }
        projectEsService.update(response.projectID) {
            it.addStatusAggregateID(response.projectID, taskResponse.aggregateID)
        }

        return response
    }

    @PostMapping("/update")
    fun updateProject(
        @RequestParam id: UUID,
        @RequestParam name: String,
    ) : ProjectUpdatedEvent {
        val response = projectEsService.update(id) { it.updateProject(id, name) }

        return response
    }

    @GetMapping("/{id}")
    fun getProject(@PathVariable id: UUID) : ProjectAndProjectMembersAggregateState? {
        return projectEsService.getState(id)
    }
}