package ru.quipy.commands

import ru.quipy.api.*
import ru.quipy.entities.Color
import ru.quipy.logic.TaskStatusAndTasksAggregateState
import java.util.*

fun TaskStatusAndTasksAggregateState.addTaskAssignee(
    taskID: UUID,
    memberID: UUID,
): TaskAssigneeAddedEvent {
    val task = tasks[taskID] ?: throw NullPointerException("Task $taskID does not exist")
    if (task.assignees.contains(memberID))
        throw IllegalArgumentException("ProjectMember $memberID is already assigned to task $taskID")

    return TaskAssigneeAddedEvent(
        taskID = taskID,
        memberID = memberID,
    )
}

fun TaskStatusAndTasksAggregateState.changeStatusForTask(
    taskID: UUID,
    statusID: UUID,
): StatusChangedForTaskEvent {
    if (!statuses.containsKey(statusID))
        throw NullPointerException("Status $statusID does not exist")

    if (!tasks.containsKey(taskID))
        throw NullPointerException("Task $taskID does not exist")

    return StatusChangedForTaskEvent(
        taskID = taskID,
        statusID = statusID,
    )
}

fun TaskStatusAndTasksAggregateState.changeTaskStatusPriority(
    statusID: UUID,
    priority: Int,
): StatusPriorityChangedEvent {
    if (!statuses.containsKey(statusID))
        throw IllegalArgumentException("Status $statusID does not exist")

    if (priority > statuses.size || priority < 1)
        throw IllegalArgumentException("Priority $priority is out of bounds")

    return StatusPriorityChangedEvent(
        statusID = statusID,
        priority = priority,
    )
}


fun TaskStatusAndTasksAggregateState.createTask(
    id: UUID,
    name: String,
    description: String,
    statusID: UUID,
): TaskCreatedEvent {
    return TaskCreatedEvent(
        taskID = id,
        taskName = name,
        description = description,
        statusID = statusID,
    )
}

fun TaskStatusAndTasksAggregateState.deleteTaskStatus(
    statusID: UUID,
): StatusDeletedEvent {
    if (!statuses.containsKey(statusID))
        throw IllegalArgumentException("Status $statusID does not exist")

    if (tasks.values.any { it.statusID == statusID })
        throw IllegalStateException("Task or tasks with status $statusID exists")

    return StatusDeletedEvent(
        statusID = statusID,
    )
}

fun TaskStatusAndTasksAggregateState.updateTask(
    id: UUID,
    name: String,
    description: String,
): TaskUpdatedEvent {
    return TaskUpdatedEvent(
        taskID = id,
        taskName = name,
        description = description,
    )
}

fun TaskStatusAndTasksAggregateState.createTaskStatus(
    statusID: UUID,
    statusName: String,
    aggregateID: UUID,
    color: Color,
    projectID: UUID?
): TaskStatusCreatedEvent {
    return TaskStatusCreatedEvent(
        statusID = statusID,
        statusName = statusName,
        aggregateID = aggregateID,
        color = color,
        projectID = projectID,
    )
}