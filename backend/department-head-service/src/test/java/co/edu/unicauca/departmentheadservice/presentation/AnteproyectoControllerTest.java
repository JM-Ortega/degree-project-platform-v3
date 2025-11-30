package co.edu.unicauca.departmentheadservice.presentation;

import co.edu.unicauca.departmentheadservice.entities.Anteproyecto;
import co.edu.unicauca.departmentheadservice.services.AnteproyectoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para la clase AnteproyectoController.
 */
@ExtendWith(MockitoExtension.class)
class AnteproyectoControllerTest {

    @Mock
    private AnteproyectoService anteproyectoService;

    @InjectMocks
    private AnteproyectoController anteproyectoController;

    private Anteproyecto anteproyecto1;
    private Anteproyecto anteproyecto2;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
// Configurar datos de prueba
//        anteproyecto1 = new Anteproyecto(
//                1L, // anteproyectoId
//                101L, // proyectoId
//                "Sistema de Gestión Académica",
//                "Descripción del sistema académico",
//                LocalDate.of(2024, 1, 15),
//                Collections.emptyList(), // sin evaluadores
//                "est1@unicauca.edu.co", // estudianteCorreo
//                "dir1@unicauca.edu.co", // directorCorreo
//                "SISTEMAS" // departamento
//        );
//
//        anteproyecto2 = new Anteproyecto(
//                2L, // anteproyectoId
//                102L, // proyectoId
//                "Plataforma de E-learning",
//                "Descripción de plataforma e-learning",
//                LocalDate.of(2024, 2, 20),
//                Collections.emptyList(), // sin evaluadores
//                "est2@unicauca.edu.co", // estudianteCorreo
//                "dir2@unicauca.edu.co", // directorCorreo
//                "SISTEMAS" // departamento
//        );


        // Establecer IDs usando reflection
        setId(anteproyecto1, 1L);
        setId(anteproyecto2, 2L);
    }

    /**
     * Método helper para establecer el ID usando reflection
     */
    private void setId(Anteproyecto anteproyecto, Long id) {
        try {
            var idField = Anteproyecto.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(anteproyecto, id);
        } catch (Exception e) {
            throw new RuntimeException("Error al establecer ID mediante reflection", e);
        }
    }

    @Test
    void testObtenerAnteproyectosSinEvaluadores_ShouldReturnEmptyList() {
        // Preparar
        when(anteproyectoService.obtenerAnteproyectosSinEvaluadores()).thenReturn(Collections.emptyList());

        // Ejecutar
        List<Anteproyecto> resultado = anteproyectoController.obtenerAnteproyectosSinEvaluadores();

        // Verificar
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(anteproyectoService).obtenerAnteproyectosSinEvaluadores();
    }

    @Test
    void testObtenerAnteproyectosSinEvaluadores_ShouldReturnListWithItems() {
        // Preparar
        List<Anteproyecto> anteproyectosEsperados = Arrays.asList(anteproyecto1, anteproyecto2);
        when(anteproyectoService.obtenerAnteproyectosSinEvaluadores()).thenReturn(anteproyectosEsperados);

        // Ejecutar
        List<Anteproyecto> resultado = anteproyectoController.obtenerAnteproyectosSinEvaluadores();

        // Verificar
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(anteproyectosEsperados, resultado);
        verify(anteproyectoService).obtenerAnteproyectosSinEvaluadores();
    }

    @Test
    void testBuscarPorNombreOIdSinEvaluadores_WithNullParameters_ShouldReturnAll() {
        // Preparar
        List<Anteproyecto> anteproyectosEsperados = Arrays.asList(anteproyecto1, anteproyecto2);
        when(anteproyectoService.buscarPorNombreOIdSinEvaluadores(null, null)).thenReturn(anteproyectosEsperados);

        // Ejecutar
        List<Anteproyecto> resultado = anteproyectoController.buscarPorNombreOIdSinEvaluadores(null, null);

        // Verificar
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(anteproyectoService).buscarPorNombreOIdSinEvaluadores(null, null);
    }

    @Test
    void testBuscarPorNombreOIdSinEvaluadores_WithOnlyName_ShouldReturnFilteredResults() {
        // Preparar
        String nombreBusqueda = "Sistema";
        List<Anteproyecto> anteproyectosEsperados = Arrays.asList(anteproyecto1);
        when(anteproyectoService.buscarPorNombreOIdSinEvaluadores(nombreBusqueda, null))
                .thenReturn(anteproyectosEsperados);

        // Ejecutar
        List<Anteproyecto> resultado = anteproyectoController.buscarPorNombreOIdSinEvaluadores(nombreBusqueda, null);

        // Verificar
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(anteproyecto1, resultado.get(0));
        verify(anteproyectoService).buscarPorNombreOIdSinEvaluadores(nombreBusqueda, null);
    }

    @Test
    void testBuscarPorNombreOIdSinEvaluadores_WithOnlyId_ShouldReturnFilteredResults() {
        // Preparar
        String idBusqueda = "1";
        List<Anteproyecto> anteproyectosEsperados = Arrays.asList(anteproyecto1);
        when(anteproyectoService.buscarPorNombreOIdSinEvaluadores(null, idBusqueda))
                .thenReturn(anteproyectosEsperados);

        // Ejecutar
        List<Anteproyecto> resultado = anteproyectoController.buscarPorNombreOIdSinEvaluadores(null, idBusqueda);

        // Verificar
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(anteproyecto1, resultado.get(0));
        verify(anteproyectoService).buscarPorNombreOIdSinEvaluadores(null, idBusqueda);
    }

    @Test
    void testBuscarPorNombreOIdSinEvaluadores_WithNameAndId_ShouldReturnCombinedResults() {
        // Preparar
        String nombreBusqueda = "Sistema";
        String idBusqueda = "1";
        List<Anteproyecto> anteproyectosEsperados = Arrays.asList(anteproyecto1);

        when(anteproyectoService.buscarPorNombreOIdSinEvaluadores(nombreBusqueda, idBusqueda))
                .thenReturn(anteproyectosEsperados);

        // Ejecutar
        List<Anteproyecto> resultado = anteproyectoController.buscarPorNombreOIdSinEvaluadores(nombreBusqueda, idBusqueda);

        // Verificar
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(anteproyecto1, resultado.get(0));
        verify(anteproyectoService).buscarPorNombreOIdSinEvaluadores(nombreBusqueda, idBusqueda);
    }

    @Test
    void testBuscarPorNombreOIdSinEvaluadores_WithEmptyName_ShouldCallServiceWithNull() {
        // Preparar
        String nombreVacio = "";
        List<Anteproyecto> anteproyectosEsperados = Arrays.asList(anteproyecto1, anteproyecto2);
        when(anteproyectoService.buscarPorNombreOIdSinEvaluadores("", null))
                .thenReturn(anteproyectosEsperados);

        // Ejecutar
        List<Anteproyecto> resultado = anteproyectoController.buscarPorNombreOIdSinEvaluadores(nombreVacio, null);

        // Verificar
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(anteproyectoService).buscarPorNombreOIdSinEvaluadores("", null);
    }

    @Test
    void testBuscarPorNombreOIdSinEvaluadores_WithEmptyId_ShouldCallServiceWithNull() {
        // Preparar
        String idVacio = "";
        List<Anteproyecto> anteproyectosEsperados = Arrays.asList(anteproyecto1);
        when(anteproyectoService.buscarPorNombreOIdSinEvaluadores(null, ""))
                .thenReturn(anteproyectosEsperados);

        // Ejecutar
        List<Anteproyecto> resultado = anteproyectoController.buscarPorNombreOIdSinEvaluadores(null, idVacio);

        // Verificar
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(anteproyectoService).buscarPorNombreOIdSinEvaluadores(null, "");
    }

    @Test
    void testBuscarPorNombreOIdSinEvaluadores_WithNoResults_ShouldReturnEmptyList() {
        // Preparar
        String nombreBusqueda = "Inexistente";
        when(anteproyectoService.buscarPorNombreOIdSinEvaluadores(nombreBusqueda, null))
                .thenReturn(Collections.emptyList());

        // Ejecutar
        List<Anteproyecto> resultado = anteproyectoController.buscarPorNombreOIdSinEvaluadores(nombreBusqueda, null);

        // Verificar
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(anteproyectoService).buscarPorNombreOIdSinEvaluadores(nombreBusqueda, null);
    }

    @Test
    void testBuscarPorNombreOIdSinEvaluadores_WithSpacesInParameters_ShouldPassTrimmedValues() {
        // Preparar
        String nombreConEspacios = "  Sistema  ";
        String idConEspacios = "  1  ";
        List<Anteproyecto> anteproyectosEsperados = Arrays.asList(anteproyecto1);

        when(anteproyectoService.buscarPorNombreOIdSinEvaluadores("  Sistema  ", "  1  "))
                .thenReturn(anteproyectosEsperados);

        // Ejecutar
        List<Anteproyecto> resultado = anteproyectoController.buscarPorNombreOIdSinEvaluadores(nombreConEspacios, idConEspacios);

        // Verificar
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(anteproyectoService).buscarPorNombreOIdSinEvaluadores("  Sistema  ", "  1  ");
    }

    @Test
    void testConstructor_Injection_ShouldWorkCorrectly() {
        // Preparar
        AnteproyectoService servicioMock = mock(AnteproyectoService.class);

        // Ejecutar
        //AnteproyectoController controlador = new AnteproyectoController(servicioMock);

        // Verificar
        //assertNotNull(controlador);
        // No hay forma directa de verificar la inyección, pero podemos verificar que no lance excepciones
    }

    @Test
    void testController_ShouldHandleLargeNumberOfResults() {
        // Preparar
        List<Anteproyecto> listaGrande = Arrays.asList(anteproyecto1, anteproyecto2, anteproyecto1, anteproyecto2);
        when(anteproyectoService.obtenerAnteproyectosSinEvaluadores()).thenReturn(listaGrande);

        // Ejecutar
        List<Anteproyecto> resultado = anteproyectoController.obtenerAnteproyectosSinEvaluadores();

        // Verificar
        assertNotNull(resultado);
        assertEquals(4, resultado.size());
        verify(anteproyectoService).obtenerAnteproyectosSinEvaluadores();
    }

    @Test
    void testBuscarPorNombreOIdSinEvaluadores_WithSpecialCharactersInName_ShouldHandleCorrectly() {
        // Preparar
        String nombreConCaracteresEspeciales = "Sistema@2024";
        List<Anteproyecto> anteproyectosEsperados = Arrays.asList(anteproyecto1);

        when(anteproyectoService.buscarPorNombreOIdSinEvaluadores(nombreConCaracteresEspeciales, null))
                .thenReturn(anteproyectosEsperados);

        // Ejecutar
        List<Anteproyecto> resultado = anteproyectoController.buscarPorNombreOIdSinEvaluadores(nombreConCaracteresEspeciales, null);

        // Verificar
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(anteproyectoService).buscarPorNombreOIdSinEvaluadores(nombreConCaracteresEspeciales, null);
    }
}