package co.edu.unicauca.academicprojectservice.adapter.out.persistence.repository;

import co.edu.unicauca.academicprojectservice.adapter.out.persistence.entity.Docente;
import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, UUID> {


    //----


    Optional<Docente> findByCorreo(String correo);

    @Query("""
        SELECT CONCAT(d.nombres, ' ', d.apellidos)
        FROM Docente d
        WHERE d.id = :id
    """)
    String findNombreDocenteById(@Param("id") UUID id);

    @Query("""
        SELECT COUNT(p)
        FROM Docente d
        JOIN d.trabajosComoDirector p
        WHERE d.correo = :correo
          AND p.estadoProyecto <> :estadoProyecto
    """)
    int countByDocenteCorreoAndEstadoNot(@Param("correo") String correo, @Param("estadoProyecto") EstadoProyecto estadoProyecto);
}
