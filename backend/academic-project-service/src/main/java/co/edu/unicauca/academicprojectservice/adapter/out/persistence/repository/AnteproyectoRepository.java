package co.edu.unicauca.academicprojectservice.adapter.out.persistence.repository;

import co.edu.unicauca.academicprojectservice.domain.model.Anteproyecto;
import co.edu.unicauca.academicprojectservice.application.dto.AnteproyectoDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnteproyectoRepository extends JpaRepository<Anteproyecto, Long> {

    @Query("""
        SELECT new co.edu.unicauca.academicprojectservice.application.dto.AnteproyectoDTO(
           a.blob,
           a.descripcion,
           e.correo,
           CONCAT(e.nombres, ' ', e.apellidos),
           a.fechaCreacion,
           p.id,
           a.nombreArchivo,
           a.titulo
        )
        FROM Proyecto p
        JOIN p.anteproyecto a
        JOIN p.estudiantes e
        JOIN p.director d
        WHERE LOWER(d.correo) = LOWER(:correo)
            AND (
             :filtro IS NULL OR :filtro = '' OR
             LOWER(a.titulo) LIKE LOWER(CONCAT('%', :filtro, '%')) OR\s
             LOWER(a.descripcion) LIKE LOWER(CONCAT('%', :filtro, '%')) OR
             LOWER(e.nombres) LIKE LOWER(CONCAT('%', :filtro, '%')) OR
             LOWER(e.apellidos) LIKE LOWER(CONCAT('%', :filtro, '%')) OR
             LOWER(e.correo) LIKE LOWER(CONCAT('%', :filtro, '%'))
            )
        ORDER BY a.fechaCreacion DESC
    """)
    List<AnteproyectoDTO> listarAnteproyectosPorCorreoDocente(
            @Param("correo") String correo,
            @Param("filtro") String filtro
    );
}
