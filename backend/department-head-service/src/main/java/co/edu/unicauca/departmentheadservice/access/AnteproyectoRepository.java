package co.edu.unicauca.departmentheadservice.access;

import co.edu.unicauca.departmentheadservice.entities.Anteproyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnteproyectoRepository extends JpaRepository<Anteproyecto, UUID> {

    // Anteproyectos sin evaluadores
    List<Anteproyecto> findByEvaluadoresIsEmpty();

    // Anteproyectos sin evaluadores filtrando por título
    List<Anteproyecto> findByEvaluadoresIsEmptyAndTituloContainingIgnoreCase(String titulo);

    // Anteproyectos sin evaluadores filtrando por anteproyectoId (UUID de dominio)
    List<Anteproyecto> findByEvaluadoresIsEmptyAndAnteproyectoId(UUID anteproyectoId);

    // Usado en el seeder para saber si ya existe
    boolean existsByAnteproyectoId(UUID anteproyectoId);

    Anteproyecto findAnteproyectoByAnteproyectoId(UUID anteproyectoId);
}
