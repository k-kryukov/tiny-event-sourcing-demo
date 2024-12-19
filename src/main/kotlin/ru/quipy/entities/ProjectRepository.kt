package ru.quipy.entities

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ProjectRepository: JpaRepository<ProjectEntity, UUID>