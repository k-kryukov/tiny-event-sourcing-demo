package ru.quipy.projections

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TaskProjectionRepository: JpaRepository<TaskProjectionEntity, UUID> {
    fun findAllByStatusID(id: UUID) : MutableList<TaskProjectionEntity>
}