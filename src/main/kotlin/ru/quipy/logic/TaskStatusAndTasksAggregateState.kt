package ru.quipy.logic

import ru.quipy.api.StatusChangedForTaskEvent
import ru.quipy.api.StatusDeletedEvent
import ru.quipy.api.StatusPositionChangedEvent
import ru.quipy.api.TaskAssigneeAddedEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskStatusAndTasksAggregate
import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.api.TaskUpdatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.entities.TaskEntity
import ru.quipy.entities.TaskStatusEntity
import java.util.UUID

class TaskStatusAndTasksAggregateState: AggregateState<UUID, TaskStatusAndTasksAggregate> {

    private lateinit var id: UUID
    private lateinit var projectID: UUID
    internal var statuses = mutableMapOf<UUID, TaskStatusEntity>()
    internal var tasks = mutableMapOf<UUID, TaskEntity>()

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    override fun getId() = id

    fun getTasks() = tasks.values.toList()

    fun getStatuses() = statuses.values.toList()

    fun getTaskByID(id: UUID) = tasks[id]

    fun getStatusByID(id: UUID) = statuses[id]

    @StateTransitionFunc
    fun statusCreatedApply(event: TaskStatusCreatedEvent) {
        id = event.aggregateID
        if (event.projectID != null)
            projectID = event.projectID

        statuses[event.statusID] = TaskStatusEntity(
            id = event.statusID,
            name = event.statusName,
            color = event.color,
            projectID = projectID,
            position = statuses.size + 1
        )
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun statusDeletedApply(event: StatusDeletedEvent) {
        val status = statuses[event.statusID] ?: throw NullPointerException("Status ${event.statusID} does not exist")

        statuses.entries.forEach {
            if (it.value.position > status.position) {
                val tmp = it.value
                tmp.position -= 1
                statuses[it.key] = tmp
            }
        }

        statuses.remove(event.statusID)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun statusPositionChangedApply(event: StatusPositionChangedEvent) {
        val status = statuses[event.statusID] ?: throw NullPointerException("Status ${event.statusID} does not exist")

        val oldPosition = status.position

        if (event.position < oldPosition) {
            statuses.entries.forEach {
                if (it.value.position >= event.position && it.value.position < oldPosition) {
                    val tmp = it.value
                    tmp.position += 1
                    statuses[it.key] = tmp
                }
            }
        } else {
            statuses.entries.forEach {
                if (it.value.position <= event.position && it.value.position > oldPosition) {
                    val tmp = it.value
                    tmp.position -= 1
                    statuses[it.key] = tmp
                }
            }
        }

        status.position = event.position
        statuses[event.statusID] = status
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun statusChangedForTaskEventApply(event: StatusChangedForTaskEvent) {
        tasks[event.taskID]?.statusID = event.statusID
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskUpdatedApply(event: TaskUpdatedEvent) {
        tasks[event.taskID]?.name = event.taskName
        tasks[event.taskID]?.description = event.description
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskID] = TaskEntity(
            id = event.taskID,
            name = event.taskName,
            description = event.description,
            projectID = projectID,
            statusID = event.statusID,
            assignees = event.assignees,
        )
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskAssigneeAddedEventApply(event: TaskAssigneeAddedEvent) {
        tasks[event.taskID]!!.assignees.add(event.memberID)
        updatedAt = event.createdAt
    }
}
