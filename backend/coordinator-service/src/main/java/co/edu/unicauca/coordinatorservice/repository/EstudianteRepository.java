package co.edu.unicauca.coordinatorservice.repository;

import co.edu.unicauca.coordinatorservice.entity.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EstudianteRepository extends JpaRepository<Estudiante, UUID> {

    Optional<Estudiante> findByEmail(String email);
}
