package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import ru.quipy.entities.Color
import java.util.UUID

const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val TASK_UPDATED_EVENT = "TASK_UPDATED_EVENT"
const val TASK_DELETED_EVENT = "TASK_DELETED_EVENT"
const val TASK_ASSIGNEE_ADDED_EVENT = "TASK_ASSIGNEE_ADDED_EVENT"
const val TASK_STATUS_CREATED_EVENT = "TASK_STATUS_CREATED_EVENT"
const val TASK_STATUS_POSITION_CHANGED_EVENT = "TASK_STATUS_POSITION_CHANGED_EVENT"
const val STATUS_CHANGED_FOR_TASK_EVENT = "STATUS_CHANGED_FOR_TASK_EVENT"

@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
    val taskID: UUID,
    val taskName: String,
    val description: String,
    val statusID: UUID,
    val assignees: MutableList<UUID> = mutableListOf(),
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskStatusAndTasksAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_DELETED_EVENT)
class StatusDeletedEvent(
    val statusID: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskStatusAndTasksAggregate>(
    name = TASK_DELETED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_STATUS_POSITION_CHANGED_EVENT)
class StatusPriorityChangedEvent(
    val statusID: UUID,
    val priority: Int,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskStatusAndTasksAggregate>(
    name = TASK_STATUS_POSITION_CHANGED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_UPDATED_EVENT)
class TaskUpdatedEvent(
    val taskID: UUID,
    val taskName: String,
    val description: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskStatusAndTasksAggregate>(
    name = TASK_UPDATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_STATUS_CREATED_EVENT)
class TaskStatusCreatedEvent(
    val statusID: UUID,
    val statusName: String,
    val aggregateID: UUID,
    val color: Color,
    val projectID: UUID?,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskStatusAndTasksAggregate>(
    name = TASK_STATUS_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = STATUS_CHANGED_FOR_TASK_EVENT)
class StatusChangedForTaskEvent(
    val taskID: UUID,
    val statusID: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskStatusAndTasksAggregate>(
    name = STATUS_CHANGED_FOR_TASK_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_ASSIGNEE_ADDED_EVENT)
class TaskAssigneeAddedEvent(
    val taskID: UUID,
    val memberID: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskStatusAndTasksAggregate>(
    name = TASK_ASSIGNEE_ADDED_EVENT,
    createdAt = createdAt,
)
