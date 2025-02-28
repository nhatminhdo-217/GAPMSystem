package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolutionRepository extends JpaRepository<Solution, Long> {
}
