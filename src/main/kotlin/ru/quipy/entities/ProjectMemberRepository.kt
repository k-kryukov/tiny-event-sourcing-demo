package ru.quipy.entities

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ProjectMemberRepository: JpaRepository<ProjectMemberEntity, UUID> {
    fun findByProjectID(id: UUID): MutableList<ProjectMemberEntity>
    fun findByUserID(id: UUID): MutableList<ProjectMemberEntity>
}