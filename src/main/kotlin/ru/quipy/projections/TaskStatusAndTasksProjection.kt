package ru.quipy.projections

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.quipy.api.*
import ru.quipy.entities.TaskStatusEntity
import ru.quipy.entities.TaskStatusRepository
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Service
class TaskStatusAndTasksProjection(
    private val taskProjectionRepository: TaskProjectionRepository,
    private val taskStatusRepository: TaskStatusRepository,
    private val subManager: AggregateSubscriptionsManager,
) {
    @PostConstruct
    fun init() {
        subManager.createSubscriber(TaskStatusAndTasksAggregate::class, "task-projection") {
            `when`(TaskStatusCreatedEvent::class) { event ->
                withContext(Dispatchers.IO) {
                    taskStatusRepository.save(
                        TaskStatusEntity(
                            event.id,
                            event.name,
                            event.projectID,
                            event.color,
                            resolveStatusesByProject(event.projectID).size + 1,
                        )
                    )
                }
            }

            `when`(TaskCreatedEvent::class) { event ->
                withContext(Dispatchers.IO) {
                    taskProjectionRepository.save(
                        TaskProjectionEntity(
                            event.taskID,
                            event.taskName,
                            event.description,
                            event.statusID,
                        )
                    )
                }
            }
            `when`(TaskUpdatedEvent::class) { event ->
                withContext(Dispatchers.IO) {
                    val task = taskProjectionRepository.getReferenceById(event.taskID)
                    task.name = event.taskName
                    task.description = event.description
                    taskProjectionRepository.save(task)
                }
            }
            `when`(StatusChangedForTaskEvent::class) { event ->
                withContext(Dispatchers.IO) {
                    val task = taskProjectionRepository.getReferenceById(event.taskID)
                    task.statusID = event.statusID
                    taskProjectionRepository.save(task)
                }
            }
        }
    }

    fun findTask(taskID: UUID): TaskProjectionEntity? {
        return taskProjectionRepository.findByIdOrNull(taskID);
    }

    fun resolveTasksByProject(projectID: UUID): List<TaskProjectionEntity> {
        return taskStatusRepository.findAllByProjectID(projectID)
            .map { status -> taskProjectionRepository.findAllByStatusID(status.id) }.flatten()
    }

    fun resolveTasksByStatus(statusID: UUID): List<TaskProjectionEntity> {
        return taskProjectionRepository.findAllByStatusID(statusID)
    }

    fun resolveStatusesByProject(projectID: UUID): List<TaskStatusEntity> {
        return taskStatusRepository.findAllByProjectID(projectID)
    }
}