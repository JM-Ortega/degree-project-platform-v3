package co.edu.unicauca.academicprojectservice.adapter.out.persistence.repository;

import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;
import co.edu.unicauca.academicprojectservice.domain.model.FormatoA;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FormatoARepository extends JpaRepository<FormatoA, UUID> {

    @Query("""
        SELECT COUNT(f)
        FROM Proyecto p
        JOIN p.formatosA f
        WHERE p.id = :proyectoId
        AND f.estado = :estadoProyecto
    """)
    int countByProyectoIdAndEstado(@Param("proyectoId") UUID proyectoId, @Param("estadoProyecto") EstadoFormatoA estadoProyecto);

    @Query("""
        SELECT MAX(f.nroVersion)
        FROM Proyecto p
        JOIN p.formatosA f
        WHERE p.id = :proyectoId
    """)
    Integer findMaxVersionByProyectoId(@Param("proyectoId") UUID proyectoId);

    @Query("""
        SELECT f
        FROM Proyecto p
        JOIN p.formatosA f
        WHERE p.id = :proyectoId
        ORDER BY f.nroVersion DESC
    """)
    List<FormatoA> findUltimoFormatoA(@Param("proyectoId") UUID proyectoId, Pageable pageable);


    @Query("""
        SELECT f
        FROM Proyecto p
        JOIN p.formatosA f
        WHERE p.id = :proyectoId
          AND f.estado = :estadoProyecto
        ORDER BY f.nroVersion DESC
    """)
    List<FormatoA> findUltimoFormatoAObservado(@Param("proyectoId") UUID proyectoId, @Param("estadoProyecto") EstadoFormatoA estadoProyecto, Pageable pageable);


    @Modifying
    @Transactional
    @Query("""
        UPDATE FormatoA f
        SET f.estado = :estadoProyecto
        WHERE f.id = (
            SELECT f2.id
            FROM Proyecto p
            JOIN p.formatosA f2
            WHERE p.id = :proyectoId
            ORDER BY f2.nroVersion DESC
            LIMIT 1
        )
    """)
    void actualizarFormatoA(@Param("proyectoId") UUID proyectoId, @Param("estadoProyecto") EstadoFormatoA estadoProyecto);


    @Query("""
        SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END
        FROM Proyecto p
        JOIN p.estudiantes e
        JOIN p.formatosA f
        WHERE LOWER(e.correo) = LOWER(:correo)
          AND f.estado = :estado
    """)
    boolean existeFormatoAAprobadoPorCorreo(@Param("correo") String correo, @Param("estado") EstadoFormatoA estado);


    @Query("""
        SELECT f
        FROM Proyecto p
        JOIN p.formatosA f
        WHERE p.id = :proyectoId
        ORDER BY f.nroVersion DESC
    """)
    Optional<FormatoA> findByProyectoId(@Param("proyectoId") UUID proyectoId);
}
