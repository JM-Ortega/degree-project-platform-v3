package co.edu.unicauca.coordinatorservice.repository;

import co.edu.unicauca.coordinatorservice.entity.Coordinador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CoordinadorRepository extends JpaRepository<Coordinador, UUID> {

    Optional<Coordinador> findByCorreo(String correo);
}
