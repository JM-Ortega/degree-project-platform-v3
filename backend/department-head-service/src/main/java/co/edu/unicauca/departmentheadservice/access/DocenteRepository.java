package co.edu.unicauca.departmentheadservice.access;

import co.edu.unicauca.departmentheadservice.entities.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, String> {

    boolean existsByEmail(String email);

    Optional<Docente> findByEmail(String email);

    @Query("""
       SELECT d2
       FROM Docente d1
       JOIN Docente d2 ON d1.departamento = d2.departamento
       WHERE d1.email = :email
         AND co.edu.unicauca.shared.contracts.model.Rol.DOCENTE
             IN elements(d2.roles)
       """)
    List<Docente> findAllDocentesByEmailDepartamento(String email);

}
