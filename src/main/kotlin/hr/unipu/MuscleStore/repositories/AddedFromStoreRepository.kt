package hr.unipu.MuscleStore.repositories

import hr.unipu.MuscleStore.entity.AddedFromStore
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AddedFromStoreRepository : JpaRepository<AddedFromStore, Int>