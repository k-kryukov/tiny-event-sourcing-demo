package ru.quipy.entities

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ProjectMemberRepository : JpaRepository<ProjectMemberEntity, UUID> {
    fun findAllByProjectID(id: UUID): MutableList<ProjectMemberEntity>
    fun findAllByUserID(id: UUID): MutableList<ProjectMemberEntity>
}