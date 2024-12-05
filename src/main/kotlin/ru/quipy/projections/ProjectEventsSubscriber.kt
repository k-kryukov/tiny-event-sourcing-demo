package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.StatusAssignedToTaskEvent
import ru.quipy.api.StatusCreatedEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Service
class ProjectEventsSubscriber {

    val logger: Logger = LoggerFactory.getLogger(ProjectEventsSubscriber::class.java)

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "project-aggregate") {

            `when`(TaskCreatedEvent::class) { event ->
                logger.info("Task created: {}", event.taskName)
            }

            `when`(StatusCreatedEvent::class) { event ->
                logger.info("Task status created: {}", event.statusId, event.statusName, event.statusColour)
            }

            `when`(StatusAssignedToTaskEvent::class) { event ->
                logger.info("Task status {} assigned to task {}: ", event.statusId, event.taskId)
            }
        }
    }
}