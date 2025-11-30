package co.edu.unicauca.departmentheadservice.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AnteproyectoTest {

    private Anteproyecto anteproyecto;
    private Docente docenteMock;

    @BeforeEach
    void setUp() {
        docenteMock = mock(Docente.class);

//        anteproyecto = new Anteproyecto(
//                10L,                          // anteproyectoId
//                20L,                          // proyectoId
//                "Proyecto de Investigación",
//                "Descripción del proyecto de investigación",
//                LocalDate.of(2025, 11, 2),
//                List.of(docenteMock),
//                "estudiante@unicauca.edu.co",
//                "director@unicauca.edu.co",
//                "SISTEMAS"
//        );
    }

    @Test
    void testConstructorAndGetters() {
        assertNotNull(anteproyecto);
        assertEquals(10L, anteproyecto.getAnteproyectoId());
        assertEquals(20L, anteproyecto.getProyectoId());
        assertEquals("Proyecto de Investigación", anteproyecto.getTitulo());
        assertEquals("Descripción del proyecto de investigación", anteproyecto.getDescripcion());
        assertEquals(LocalDate.of(2025, 11, 2), anteproyecto.getFechaCreacion());
        assertEquals("estudiante@unicauca.edu.co", anteproyecto.getEstudianteCorreo());
        assertEquals("director@unicauca.edu.co", anteproyecto.getDirectorCorreo());
        assertEquals("SISTEMAS", anteproyecto.getDepartamento());
        assertEquals(1, anteproyecto.getEvaluadores().size());
        assertSame(docenteMock, anteproyecto.getEvaluadores().get(0));
    }

    @Test
    void testSetters() {
        anteproyecto.setTitulo("Nuevo Título");
        anteproyecto.setDescripcion("Nueva descripción");
        anteproyecto.setFechaCreacion(LocalDate.of(2025, 12, 15));
        anteproyecto.setEstudianteCorreo("nuevo.estudiante@unicauca.edu.co");
        anteproyecto.setDirectorCorreo("nuevo.director@unicauca.edu.co");
        anteproyecto.setDepartamento("ELECTRÓNICA");

        assertEquals("Nuevo Título", anteproyecto.getTitulo());
        assertEquals("Nueva descripción", anteproyecto.getDescripcion());
        assertEquals(LocalDate.of(2025, 12, 15), anteproyecto.getFechaCreacion());
        assertEquals("nuevo.estudiante@unicauca.edu.co", anteproyecto.getEstudianteCorreo());
        assertEquals("nuevo.director@unicauca.edu.co", anteproyecto.getDirectorCorreo());
        assertEquals("ELECTRÓNICA", anteproyecto.getDepartamento());
    }

    @Test
    void testSetEvaluadores() {
        Docente docenteMock2 = mock(Docente.class);
        anteproyecto.setEvaluadores(List.of(docenteMock, docenteMock2));

        assertEquals(2, anteproyecto.getEvaluadores().size());
        assertSame(docenteMock, anteproyecto.getEvaluadores().get(0));
        assertSame(docenteMock2, anteproyecto.getEvaluadores().get(1));
    }

    @Test
    void testConstructorSinArgumentos() {
        Anteproyecto anteproyectoSinArgs = new Anteproyecto();
        assertNotNull(anteproyectoSinArgs);
    }
}
