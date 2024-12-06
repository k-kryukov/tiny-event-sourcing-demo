package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.UUID

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val MEMBER_CREATED_EVENT = "MEMBER_CREATED_EVENT"

@DomainEvent(name = PROJECT_CREATED_EVENT)
class ProjectCreatedEvent(
    val projectID: UUID,
    val projectName: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAndProjectMembersAggregate>(
    name = PROJECT_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_STATUS_CREATED_EVENT)
class TaskStatusCreatedForProjectEvent(
    val projectID: UUID,
    val aggregateID: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAndProjectMembersAggregate>(
    name = TASK_STATUS_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = MEMBER_CREATED_EVENT)
class ProjectMemberCreatedEvent(
    val memberID: UUID,
    val memberLogin: String,
    val memberName: String,
    val userID: UUID,
    val projectID: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAndProjectMembersAggregate>(
    name = MEMBER_CREATED_EVENT,
    createdAt = createdAt,
)
