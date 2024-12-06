package ru.quipy.commands

import ru.quipy.api.ProjectMemberCreatedEvent
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.TaskStatusCreatedForProjectEvent
import ru.quipy.logic.ProjectAndProjectMembersAggregateState
import java.util.*

fun ProjectAndProjectMembersAggregateState.addStatusAggregateID(
    projectID: UUID,
    statusAggregateID: UUID,
): TaskStatusCreatedForProjectEvent {
    return TaskStatusCreatedForProjectEvent(
        projectID = projectID,
        aggregateID = statusAggregateID,
    )
}

fun ProjectAndProjectMembersAggregateState.createProjectMember(
    memberID: UUID,
    login: String?,
    name: String?,
    userID: UUID?,
    projectID: UUID
): ProjectMemberCreatedEvent {
    if (login == null || name == null || userID == null)
        throw IllegalArgumentException("User $userID was not found")

    if (members.entries.firstOrNull { it.value.userID == userID } != null)
        throw IllegalArgumentException("User $userID is already member of project $projectID")

    return ProjectMemberCreatedEvent(
        memberID = memberID,
        memberLogin = login,
        memberName = name,
        userID = userID,
        projectID = projectID,
    )
}

fun ProjectAndProjectMembersAggregateState.createProject(id: UUID, name: String): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectID = id,
        projectName = name,
    )
}
