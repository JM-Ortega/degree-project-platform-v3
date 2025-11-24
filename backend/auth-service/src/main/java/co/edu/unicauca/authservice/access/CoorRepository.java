package co.edu.unicauca.authservice.access;

import co.edu.unicauca.authservice.domain.entities.Coordinador;
import co.edu.unicauca.shared.contracts.model.Programa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoorRepository extends JpaRepository<Coordinador, String> {
    Optional<Coordinador> findByPrograma(Programa programa);
}
