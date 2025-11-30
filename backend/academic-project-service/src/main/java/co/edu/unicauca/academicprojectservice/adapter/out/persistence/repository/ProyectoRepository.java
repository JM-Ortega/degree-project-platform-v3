package co.edu.unicauca.academicprojectservice.adapter.out.persistence.repository;

import co.edu.unicauca.academicprojectservice.adapter.out.persistence.entity.Proyecto;
import co.edu.unicauca.academicprojectservice.application.dto.ProyectoInfoDTO;
import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import co.edu.unicauca.shared.contracts.model.TipoProyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, UUID> {

    @Query("""
                SELECT new co.edu.unicauca.academicprojectservice.application.dto.ProyectoInfoDTO(
                    p.id,
                    p.titulo,
                    p.tipoProyecto,
                    p.estadoProyecto,
                    CONCAT(e.nombres, ' ', e.apellidos),
                    e.correo
                )
                FROM Proyecto p
                JOIN p.estudiantes e
                WHERE 
                    (p.director.id = :docenteId)
                    AND (
                        :filtro IS NULL OR :filtro = '' OR
                        LOWER(p.titulo) LIKE LOWER(CONCAT('%', :filtro, '%')) OR
                        LOWER(e.nombres) LIKE LOWER(CONCAT('%', :filtro, '%')) OR
                        LOWER(e.apellidos) LIKE LOWER(CONCAT('%', :filtro, '%')) OR
                        LOWER(e.correo) LIKE LOWER(CONCAT('%', :filtro, '%'))
                    )
                ORDER BY p.id DESC
            """)
    List<ProyectoInfoDTO> listarInfoPorDocente(@Param("docenteId") UUID docenteId,
                                               @Param("filtro") String filtro);


    @Modifying
    @Transactional
    @Query("""
        UPDATE Proyecto p
        SET p.estadoProyecto = :estadoProyecto
        WHERE p.id = :proyectoId
    """)
    void actualizarEstadoProyecto(@Param("proyectoId") UUID proyectoId, @Param("estadoProyecto") EstadoProyecto estadoProyecto);


    @Query("""
        SELECT CAST(p.estadoProyecto AS string)
        FROM Proyecto p
        WHERE p.id = :proyectoId
    """)
    String getEstadoProyecto(@Param("proyectoId") UUID proyectoId);


    boolean existsById(UUID proyectoId);


    @Query("""
        SELECT COUNT(p)
        FROM Proyecto p
        JOIN p.director d
        WHERE p.tipoProyecto = :tipo
        AND p.estadoProyecto = :estadoProyecto
        AND d.correo = :correoDocente
    """)
    int countProyectosByEstadoYTipo(@Param("tipo") TipoProyecto tipo, @Param("estadoProyecto") EstadoProyecto estadoProyecto, @Param("correoDocente") String correoDocente);


    @Query("""
        SELECT p
        FROM Proyecto p
        JOIN p.estudiantes e
        WHERE LOWER(e.correo) = LOWER(:correo)
        AND p.estadoProyecto = :estado
    """)
    Optional<Proyecto> findByEstudiantesCorreoIgnoreCaseAndEstadoProyecto(@Param("correo") String correo, @Param("estado") EstadoProyecto estado);


    @Query("""
        SELECT p
        FROM Proyecto p
        JOIN p.estudiantes e
        WHERE LOWER(e.correo) = LOWER(:correo)
    """)
    List<Proyecto> findByEstudianteCorreo(@Param("correo") String correo);

    // Eh si algo falla capz y es esta query, la hice al ojo
    @Query("""
        SELECT p
        FROM Proyecto p
        JOIN p.estudiantes e
        WHERE LOWER(e.correo) = LOWER(:correo)
        AND p.estadoProyecto != "FORMATOA_RECHAZADO"
    """)
    Optional<Proyecto> findByEstudianteCorreoTramite(@Param("correo") String correo);
}

