package co.edu.unicauca.authservice.access;

import co.edu.unicauca.authservice.domain.entities.JefeDeDepartamento;
import co.edu.unicauca.shared.contracts.model.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JefeRepository extends JpaRepository<JefeDeDepartamento, String> {
    Optional<JefeDeDepartamento> findByDepartamento(Departamento departamento);
}
