package ru.quipy.projections

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.quipy.api.ProjectAndProjectMembersAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.ProjectMemberCreatedEvent
import ru.quipy.entities.*
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.UUID
import javax.annotation.PostConstruct

@Service
class ProjectAndProjectMembersProjection(
    private val projectRepository: ProjectRepository,
    private val projectMemberRepository: ProjectMemberRepository,
    private val subManager: AggregateSubscriptionsManager,
) {
    @PostConstruct
    fun init() {
        subManager.createSubscriber(ProjectAndProjectMembersAggregate::class, "project-projection") {
            `when`(ProjectMemberCreatedEvent::class) { event ->
                withContext(Dispatchers.IO) {
                    projectMemberRepository.save(
                        ProjectMemberEntity(
                            event.memberID,
                            event.memberName,
                            event.memberLogin,
                            event.userID,
                            event.projectID
                        )
                    )
                }
            }
            `when`(ProjectCreatedEvent::class) { event ->
                withContext(Dispatchers.IO) {
                    projectRepository.save(
                        ProjectEntity(
                            event.projectID,
                            event.projectName
                        )
                    )
                }
            }
        }
    }

    fun findProject(projectID : UUID) : ProjectEntity? {
        return projectRepository.findByIdOrNull(projectID)
    }

    fun resolveProjectMembersByProject(projectID: UUID): List<ProjectMemberEntity> {
        return projectMemberRepository.findByProjectID(projectID)
    }

    fun resolveProjectsByUser(userID: UUID): List<ProjectEntity> {
        return projectRepository.findAllById(
            projectMemberRepository.findByUserID(userID).map { member -> member.projectID }
        )
    }
}