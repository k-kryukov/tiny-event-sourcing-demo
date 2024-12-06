package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskUpdatedEvent
import ru.quipy.api.UserAggregate
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent

@Service
@AggregateSubscriber(
        aggregateClass = UserAggregate::class, subscriberName = "task-status-and-tasks-subs-stream"
)
class AnnotationBasedTaskStatusAndTasksEventsSubscriber {

    val logger: Logger = LoggerFactory.getLogger(AnnotationBasedTaskStatusAndTasksEventsSubscriber::class.java)

    @SubscribeEvent
    fun taskCreatedSubscriber(event: TaskCreatedEvent) {
//        TODO("taskCreatedSubscriber")
    }

    @SubscribeEvent
    fun tagCreatedSubscriber(event: TaskUpdatedEvent) {
//        TODO("tagCreatedSubscriber")
    }
}
