package co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.repository;

import co.edu.unicauca.academicprojectservice.domain.model.DocenteId;
import co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.entity.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, UUID> {
    Optional<Docente> findByCorreo(String correo);

    @Query("""
        SELECT CONCAT(d.nombres, ' ', d.apellidos)
        FROM Docente d
        WHERE d.id = :id
    """)
    String findNombreDocenteById(@Param("id") UUID id);

}
