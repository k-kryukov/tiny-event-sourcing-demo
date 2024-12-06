package ru.quipy.logic

import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.entities.ProjectMemberEntity
import ru.quipy.entities.ProjectEntity
import java.util.UUID
import ru.quipy.api.*

class ProjectAndProjectMembersAggregateState : AggregateState<UUID, ProjectAndProjectMembersAggregate> {

    private lateinit var id: UUID
    private lateinit var statusesAndTasksAggregateID: UUID
    private lateinit var project: ProjectEntity
    internal var members = mutableMapOf<UUID, ProjectMemberEntity>()

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    override fun getId() = project.id

    fun getStatusesAndTasksAggregateId() = statusesAndTasksAggregateID

    fun getProjectMemberByID(id: UUID) = members[id]

    fun getName() = project.name

    fun getProjectMembers() = members.values.toList()

    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        id = event.projectID
        project = ProjectEntity(
            id = event.projectID,
            name = event.projectName,
        )
        createdAt = event.createdAt
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun projectUpdatedApply(event: ProjectUpdatedEvent) {
        project = ProjectEntity(
            id = event.projectID,
            name = event.projectName,
        )

        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun memberCreatedApply(event: ProjectMemberCreatedEvent) {
        members[event.memberID] = ProjectMemberEntity(
            id = event.memberID,
            name = event.memberName,
            login = event.memberLogin,
            userID = event.userID,
            projectID = event.projectID,
        )
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskStatusCreatedApply(event: TaskStatusCreatedForProjectEvent) {
        if (event.projectID == project.id) {
            statusesAndTasksAggregateID = event.aggregateID
            updatedAt = event.createdAt
        }
    }
}
