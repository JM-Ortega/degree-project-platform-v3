package co.edu.unicauca.departmentheadservice.entities;

import co.edu.unicauca.shared.contracts.model.Departamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocenteTest {

    private Docente docente;

    @BeforeEach
//    void setUp() {
//        // Instanciamos la clase Docente con datos de prueba
//        docente = new Docente("12345", "Juan Ortega", "juan@unicauca.edu.co", Departamento.SISTEMAS);
//    }

    @Test
    void testConstructorAndGetters() {
        assertNotNull(docente);
        assertEquals("12345", docente.getPersonaId());
        assertEquals("Juan Ortega", docente.getNombre());
        assertEquals("juan@unicauca.edu.co", docente.getEmail());
    }

    @Test
    void testSetters() {
        // Cambiar valores a través de los setters
//        docente.setPersonaId("67890");
        docente.setNombre("Carlos Perez");
        docente.setEmail("carlos@unicauca.edu.co");

        // Verificar que los valores se actualizan correctamente
        assertEquals("67890", docente.getPersonaId());
        assertEquals("Carlos Perez", docente.getNombre());
        assertEquals("carlos@unicauca.edu.co", docente.getEmail());
    }

    @Test
    void testConstructorSinArgumentos() {
        // Crear un Docente sin argumentos (que debería ser usado por JPA)
        Docente docenteSinArgumentos = new Docente();
        assertNotNull(docenteSinArgumentos);
    }
}
