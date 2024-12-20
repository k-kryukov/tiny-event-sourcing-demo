package ru.quipy.projections

import org.springframework.stereotype.Service
import java.util.*

@Service
class ProjectionService(
    val statusAndTasksProjection: TaskStatusAndTasksProjection,
    val projectAndProjectMembersProjection: ProjectAndProjectMembersProjection,
    val userProjection: UserProjection,
) {
    fun findTask(taskID: UUID): TaskProjectionDTO? {
        return statusAndTasksProjection.findTask(taskID)?.toDto()
    }

    fun findProject(projectID: UUID): ProjectDTO? {
        return projectAndProjectMembersProjection.findProject(projectID)?.toDto(
            resolveProjectMembersByProject(projectID),
            resolveStatusesByProject(projectID)
        )
    }

    fun resolveProjectMembersByProject(projectID: UUID): List<ProjectMemberDTO> {
        return projectAndProjectMembersProjection.resolveProjectMembersByProject(projectID)
            .map { member -> member.toDto() };
    }

    fun resolveTasksByStatus(statusID: UUID): List<TaskProjectionDTO> {
        return statusAndTasksProjection.resolveTasksByStatus(statusID).map { task -> task.toDto() };
    }


    fun resolveStatusesByProject(projectID: UUID): List<TaskStatusDTO> {
        return statusAndTasksProjection.resolveStatusesByProject(projectID)
            .map { status -> status.toDto(resolveTasksByStatus(status.id)) }
    }

    fun resolveTasksByProject(projectID: UUID): List<TaskProjectionDTO> {
        return statusAndTasksProjection.resolveTasksByProject(projectID).map { task -> task.toDto() }
    }

    fun resolveProjectsByUser(userID: UUID): List<ProjectDTO> {
        return projectAndProjectMembersProjection.resolveProjectsByUser(userID)
            .map { project ->
                project.toDto(
                    resolveProjectMembersByProject(project.id),
                    resolveStatusesByProject(project.id)
                )
            };
    }

    fun findUser(userID: UUID): UserDTO? {
        return userProjection.findUser(userID)?.toDto()
    }

    fun listUsersByName(name: String): List<UserDTO> {
        return userProjection.listUsersByName(name).map { user -> user.toDto() }
    }
}