package ru.quipy.logic

import ru.quipy.api.*
import java.util.*


fun ProjectAggregateState.create(id: UUID, projectName: String, creatorID: UUID): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = id,
        projectName = projectName,
        creatorID = creatorID
    )
}

fun ProjectAggregateState.update(projectName: String): ProjectUpdatedEvent {
    return ProjectUpdatedEvent(
        projectId = this.getId(),
        projectName = projectName
    )
}

fun ProjectAggregateState.addTask(name: String, description: String): TaskCreatedEvent {
    return TaskCreatedEvent(projectId = this.getId(), taskId = UUID.randomUUID(), taskName = name, description = description)
}

fun ProjectAggregateState.editTask(taskID: UUID, name: String, description: String): TaskEditedEvent {
    return TaskEditedEvent(projectId = this.getId(), taskId = taskID, taskName = name, description = description)
}

fun ProjectAggregateState.createStatus(name: String, colour: String, priority: Int = 0): StatusCreatedEvent {
    if (statuses.values.any { it.name == name }) {
        throw IllegalArgumentException("Task status already exists: $name")
    }
    return StatusCreatedEvent(
        projectId = this.getId(),
        statusId = UUID.randomUUID(),
        statusName = name,
        statusColour = colour,
        priority = priority)
}

fun ProjectAggregateState.addProjectMember(userID: UUID): ProjectMemberAddedEvent {
    if (projectMembers.any { it == userID }) {
        throw IllegalArgumentException("ProjectMember already added: $userID")
    }
    return ProjectMemberAddedEvent(
        projectId = this.getId(),
        projectMemberId = userID
    )
}

fun ProjectAggregateState.addAssigneeToTask(taskId: UUID, projectMemberId: UUID): AssigneeAddedToTaskEvent {
    if (!projectMembers.any { it == projectMemberId }) {
        throw IllegalArgumentException("No such projectMember: ${projectMemberId}")
    }
    return AssigneeAddedToTaskEvent(
        projectId = this.getId(),
        taskId = taskId,
        projectMemberId = projectMemberId)
}

fun ProjectAggregateState.assignStatusToTask(statusId: UUID, taskId: UUID): StatusAssignedToTaskEvent {
    if (!statuses.containsKey(statusId)) {
        throw IllegalArgumentException("Task status doesn't exists: $statusId")
    }

    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    return StatusAssignedToTaskEvent(projectId = this.getId(), statusId = statusId, taskId = taskId)
}

fun ProjectAggregateState.updateStatusPriority(statusId: UUID, priority: Int): StatusPriorityUpdatedEvent {
    if (!statuses.containsKey(statusId)) {
        throw IllegalArgumentException("Task status doesn't exists: $statusId")
    }

    return StatusPriorityUpdatedEvent(projectId = this.getId(), statusId = statusId, priority = priority)
}

fun ProjectAggregateState.deleteStatus(statusId: UUID): StatusDeletedEvent {
    if (!statuses.containsKey(statusId)) {
        throw IllegalArgumentException("Task status doesn't exists: $statusId")
    }

    return StatusDeletedEvent(projectId = this.getId(), statusId = statusId)
}