package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import ru.quipy.logic.StatusEntity
import ru.quipy.logic.ProjectMemberEntity
import java.util.*
import kotlin.collections.mutableMapOf

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val PROJECT_UPDATED_EVENT = "PROJECT_UPDATED_EVENT"
const val TASK_STATUS_CREATED_EVENT = "TASK_STATUS_CREATED_EVENT"
const val TASK_STATUS_ASSIGNED_TO_TASK_EVENT = "TASK_STATUS_ASSIGNED_TO_TASK_EVENT"
const val TASK_STATUS_PRIORITY_UPDATED_EVENT = "TASK_STATUS_PRIORITY_UPDATED_EVENT"
const val TASK_STATUS_DELETED_EVENT = "TASK_STATUS_DELETED_EVENT"
const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val TASK_UPDATED_EVENT = "TASK_UPDATED_EVENT"
const val PROJECT_MEMBER_ADDED_EVENT = "PROJECT_MEMBER_ADDED_EVENT"
const val ASSIGNEE_ADDED_TO_TASK_EVENT = "ASSIGNEE_ADDED_TO_TASK_EVENT"

// API
@DomainEvent(name = PROJECT_CREATED_EVENT)
class ProjectCreatedEvent(
    val projectId: UUID,
    val projectName: String,
    val creatorID: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = PROJECT_UPDATED_EVENT)
class ProjectUpdatedEvent(
    val projectId: UUID,
    val projectName: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_UPDATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_STATUS_CREATED_EVENT)
class StatusCreatedEvent(
    val projectId: UUID,
    val statusId: UUID,
    val statusName: String,
    val statusColour: String,
    val priority: Int,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_STATUS_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val taskName: String,
    val description: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = TASK_UPDATED_EVENT)
class TaskEditedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val taskName: String,
    val description: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_UPDATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = TASK_STATUS_ASSIGNED_TO_TASK_EVENT)
class StatusAssignedToTaskEvent(
    val projectId: UUID,
    val taskId: UUID,
    val statusId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_STATUS_ASSIGNED_TO_TASK_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = TASK_STATUS_PRIORITY_UPDATED_EVENT)
class StatusPriorityUpdatedEvent(
    val projectId: UUID,
    val statusId: UUID,
    val priority: Int,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_STATUS_PRIORITY_UPDATED_EVENT,
    createdAt = createdAt
)


@DomainEvent(name = TASK_STATUS_DELETED_EVENT)
class StatusDeletedEvent(
    val projectId: UUID,
    val statusId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_STATUS_DELETED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = PROJECT_MEMBER_ADDED_EVENT)
class ProjectMemberAddedEvent(
    val projectId: UUID,
    val projectMemberId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_MEMBER_ADDED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = ASSIGNEE_ADDED_TO_TASK_EVENT)
class AssigneeAddedToTaskEvent(
    val projectId: UUID,
    val taskId: UUID,
    val projectMemberId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = ASSIGNEE_ADDED_TO_TASK_EVENT,
    createdAt = createdAt
)