package co.edu.unicauca.academicprojectservice.adapter.out.persistence.repository;

import co.edu.unicauca.academicprojectservice.adapter.out.persistence.entity.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EstudianteRepository extends JpaRepository<Estudiante, UUID> {
    Optional<Estudiante> findByCorreoIgnoreCase(String correo);

    // Se cambio porque ya no se podia validar por el proyecto en estado EN_TRAMITE
    @Query("""
        SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
        FROM Proyecto p JOIN p.estudiantes e
        WHERE lower(e.correo) = lower(:correo) AND p.estadoProyecto != 'FORMATOA_RECHAZADO'
    """)
    boolean tieneProyectoActivo(@Param("correo") String correo);

}