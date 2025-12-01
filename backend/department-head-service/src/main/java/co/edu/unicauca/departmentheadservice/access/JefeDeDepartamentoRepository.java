package co.edu.unicauca.departmentheadservice.access;

import co.edu.unicauca.departmentheadservice.entities.JefeDeDepartamento;
import co.edu.unicauca.shared.contracts.model.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JefeDeDepartamentoRepository extends JpaRepository<JefeDeDepartamento, UUID> {

    Optional<JefeDeDepartamento> findByPersonaId(UUID personaId);
}
