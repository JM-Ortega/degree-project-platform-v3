package co.edu.unicauca.departmentheadservice.access;

import co.edu.unicauca.departmentheadservice.entities.Docente;
import co.edu.unicauca.shared.contracts.model.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, String> {

    boolean existsByEmail(String email);

    Optional<Docente> findByEmail(String email);

    @Query("""
       SELECT d 
       FROM Docente d
       WHERE d.departamento = (
           SELECT j.departamento 
           FROM JefeDeDepartamento j 
           WHERE j.email = :email
       )
       """)
    List<Docente> findAllDocentesByEmailDepartamento(String email);
}
