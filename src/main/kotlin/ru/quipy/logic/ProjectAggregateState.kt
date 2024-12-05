package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*
import kotlin.collections.mutableMapOf

class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var projectName: String
    var tasks = mutableMapOf<UUID, TaskEntity>()
    var statuses = mutableMapOf<UUID, StatusEntity>()
    var projectMembers = mutableSetOf<UUID>()

    override fun getId() = projectId

    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectName = event.projectName
        updatedAt = createdAt
        statuses = withDefaultStatus()
        projectMembers.add(event.creatorID)
    }

    @StateTransitionFunc
    fun projectUpdatedApply(event: ProjectUpdatedEvent) {
        projectName = event.projectName
    }

    @StateTransitionFunc
    fun statusCreatedApply(event: StatusCreatedEvent) {
        statuses[event.statusId] = StatusEntity(event.statusId, event.statusName, event.statusColour, event.priority)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun statusDeletedApply(event: StatusDeletedEvent) {
        if (tasks.filter {
            it.value.statusAssigned == event.statusId
        }.isNotEmpty())
            throw RuntimeException("Status is assigned somewhere!")

        statuses.remove(event.statusId)
    }

    @StateTransitionFunc
    fun statusPriorityUpdatedApply(event: StatusPriorityUpdatedEvent) {
        if (statuses.get(event.statusId) == null) {
            TODO("wtf")
        }

        statuses[event.statusId] = StatusEntity(
            id = statuses[event.statusId]!!.id,
            name = statuses[event.statusId]!!.name,
            colour = statuses[event.statusId]!!.colour,
            priority = event.priority
        )
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = TaskEntity(
            id = event.taskId,
            name = event.taskName,
            statusAssigned = withDefaultStatusUuid(),
            assigneesAssigned = mutableSetOf<UUID>())
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskEditedApply(event: TaskEditedEvent) {
        if (tasks[event.taskId] == null) {
            TODO("throw")
        }

        tasks[event.taskId] = TaskEntity(
            id = event.taskId,
            name = event.taskName,
            statusAssigned = tasks[event.taskId]!!.statusAssigned,
            assigneesAssigned = tasks[event.taskId]!!.assigneesAssigned
        )

        updatedAt = createdAt
    }


    @StateTransitionFunc
    fun statusAssignedApply(event: StatusAssignedToTaskEvent) {
        tasks[event.taskId]?.statusAssigned = event.statusId
            ?: throw IllegalArgumentException("No such task: ${event.taskId}")
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun projectMemberAddedApply(event: ProjectMemberAddedEvent) {
        if (projectMembers.any { it == event.projectMemberId }) {
            throw IllegalArgumentException("ProjectMember already added: ${event.projectMemberId}")
        }
        projectMembers.add(event.projectMemberId)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun assigneeAddedToTaskApply(event: AssigneeAddedToTaskEvent) {
        if (!projectMembers.any { it == event.projectMemberId }) {
            throw IllegalArgumentException("No such projectMember: ${event.projectMemberId}")
        }
        tasks[event.taskId]?.assigneesAssigned?.add(event.projectMemberId)
            ?: throw IllegalArgumentException("No such task: ${event.taskId}")
        updatedAt = createdAt
    }


    fun withDefaultStatus() : MutableMap<UUID, StatusEntity> {
        val id = UUID.randomUUID()
        val defaultStatus = StatusEntity(id = id, name = "Created", colour = "Blue", priority = 0)

        return mutableMapOf<UUID, StatusEntity>(id to defaultStatus)
    }

    fun withDefaultStatusUuid() : UUID {
        if (statuses.values.any { it.name == "Created" }) {
            val defaultStatus = statuses.values.first { it.name == "Created" }
            return defaultStatus.id
        }
        else {
            TODO("wtf")
        }
    }

    fun withAuthorProjectMember(authorUsername: String) : MutableMap<UUID, ProjectMemberEntity> {
        val projectMember = ProjectMemberEntity(username = authorUsername)

        return mutableMapOf<UUID, ProjectMemberEntity>(projectMember.id to projectMember)
    }
}

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    var statusAssigned: UUID,
    val assigneesAssigned: MutableSet<UUID>
)

data class StatusEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val colour: String,
    var priority: Int,
)

data class ProjectMemberEntity(
    val id: UUID = UUID.randomUUID(),
    val username: String,
)
