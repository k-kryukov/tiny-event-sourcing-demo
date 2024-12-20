package ru.quipy.entities

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TaskStatusRepository : JpaRepository<TaskStatusEntity, UUID> {
    fun findAllByProjectID(id: UUID): MutableList<TaskStatusEntity>
}