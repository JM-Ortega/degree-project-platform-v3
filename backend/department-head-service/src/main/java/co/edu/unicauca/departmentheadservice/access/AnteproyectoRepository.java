package co.edu.unicauca.departmentheadservice.access;

import co.edu.unicauca.departmentheadservice.entities.Anteproyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnteproyectoRepository extends JpaRepository<Anteproyecto, Long> {

    // Método existente
    List<Anteproyecto> findByEvaluadoresIsEmpty();

    // Método para buscar por título
    List<Anteproyecto> findByEvaluadoresIsEmptyAndTituloContainingIgnoreCase(String titulo);

    // Método para buscar por ID
    List<Anteproyecto> findByEvaluadoresIsEmptyAndId(Long id);

    boolean existsByAnteproyectoId(UUID anteproyectoId);


}