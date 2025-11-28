package co.edu.unicauca.coordinatorservice.repository;

import co.edu.unicauca.coordinatorservice.entity.FormatoA;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface FormatoARepository extends JpaRepository<FormatoA, Long> {
    Optional<FormatoA> findByProyectoId(UUID proyectoId);
}
