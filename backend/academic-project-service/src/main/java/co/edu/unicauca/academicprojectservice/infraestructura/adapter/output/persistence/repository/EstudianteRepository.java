package co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.repository;

import co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.entity.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EstudianteRepository extends JpaRepository<Estudiante, UUID> {
    Optional<Estudiante> findByCorreoIgnoreCase(String correo);

    @Query("""
        SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
        FROM Proyecto p JOIN p.estudiantes e
        WHERE lower(e.correo) = lower(:correo) AND p.estadoProyecto = 'EN_TRAMITE'
    """)
    boolean tieneProyectoEnTramite(@Param("correo") String correo);

}