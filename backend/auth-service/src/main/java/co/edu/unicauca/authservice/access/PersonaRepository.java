package co.edu.unicauca.authservice.access;

import co.edu.unicauca.authservice.domain.entities.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PersonaRepository extends JpaRepository<Persona, String> {
    Optional<Persona> findByUsuarioId(String usuarioId);

    @Query("""
        SELECT p.celular 
        FROM Persona p 
        WHERE p.usuario.email IN :correos
    """)
    List<String> findCelularesByUsuarioEmailIn(@Param("correos") List<String> correos);
}
