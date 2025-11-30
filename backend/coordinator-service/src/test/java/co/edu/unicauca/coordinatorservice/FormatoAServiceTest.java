package co.edu.unicauca.coordinatorservice;

import co.edu.unicauca.coordinatorservice.entity.Docente;
import co.edu.unicauca.coordinatorservice.entity.Estudiante;
import co.edu.unicauca.coordinatorservice.entity.FormatoA;
import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;
import co.edu.unicauca.coordinatorservice.repository.FormatoARepository;
import co.edu.unicauca.coordinatorservice.service.FormatoAService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class FormatoAServiceTest {

    @Mock
    private FormatoARepository formatoARepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private FormatoAService formatoAService;

    @Value("${messaging.exchange.main:mainExchange}")
    private String mainExchange = "main.exchange";

    @Value("${messaging.routing.formatAApprovedByCoordinator:formatA.approved}")
    private String routingKeyFormatAApproved = "formatA.approved";

    private FormatoA formatoBase;
    private Docente director;
    private Docente codirector;
    private List<Estudiante> estudiantes;

    @BeforeEach
    void setUp() {
        // Inyectar valores @Value manualmente
        ReflectionTestUtils.setField(formatoAService, "mainExchange", "main.exchange.test");
        ReflectionTestUtils.setField(formatoAService, "routingKeyFormatAApproved", "formatA.approved.test");

        // Crear director y codirector
        Docente director = new Docente();
        director.setEmail("director@mail.com");
        director.setCelular("12345");

        Docente codirector = new Docente();
        codirector.setEmail("co@mail.com");
        codirector.setCelular("67890");

        // Crear estudiantes
        Estudiante e1 = new Estudiante();
        e1.setEmail("est1@mail.com");
        e1.setCelular("11111");

        Estudiante e2 = new Estudiante();
        e2.setEmail("est2@mail.com");
        e2.setCelular("22222");

        List<Estudiante> estudiantes = Arrays.asList(e1, e2);

        // Crear el FormatoA base
        formatoBase = new FormatoA();
        formatoBase.setId(1L);
        formatoBase.setProyectoId(100L);
        formatoBase.setNombreProyecto("Sistema de Gestión");
        formatoBase.setNroVersion(2);
        formatoBase.setEstadoFormatoA(EstadoFormatoA.PENDIENTE);
        formatoBase.setDirector(director);
        formatoBase.setCoodirector(codirector);
        formatoBase.setEstudiantes(estudiantes);
        when(rabbitTemplate.getMessageConverter())
                .thenReturn(new Jackson2JsonMessageConverter());
    }

    @Test
    void actualizarFormato_aprobado_debeActualizarYEnviarEventos() throws IOException {
        // Arrange
        when(formatoARepository.findById(1L)).thenReturn(Optional.of(formatoBase));
        when(formatoARepository.save(any(FormatoA.class))).thenAnswer(i -> i.getArgument(0));

        MockMultipartFile file = new MockMultipartFile("archivo", "formatoA.pdf", "application/pdf", "contenido".getBytes());
        String horaActual = "2025-10-10 12:00:00";

        // Act
        FormatoA result = formatoAService.actualizarFormato(1L, file, "aprobado", "formatoA.pdf", horaActual);

        // Assert
        assertEquals(EstadoFormatoA.APROBADO, result.getEstadoFormatoA());
        assertEquals("formatoA.pdf", result.getNombreFormatoA());
        assertNotNull(result.getFechaSubida());

        verify(formatoARepository).save(any(FormatoA.class));
        verify(rabbitTemplate, times(2))
                .convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    void actualizarFormato_rechazado_debeActualizarYEnviarEventos() throws IOException {
        // Arrange
        when(formatoARepository.findById(1L)).thenReturn(Optional.of(formatoBase));
        when(formatoARepository.save(any(FormatoA.class))).thenAnswer(i -> i.getArgument(0));

        MockMultipartFile file = new MockMultipartFile("archivo", "formatoA.pdf", "application/pdf", "contenido".getBytes());
        String horaActual = "2025-10-10 12:00:00";

        // Act
        FormatoA result = formatoAService.actualizarFormato(1L, file, "rechazado", "formatoA.pdf", horaActual);

        // Assert
        assertEquals(EstadoFormatoA.OBSERVADO, result.getEstadoFormatoA());
        verify(rabbitTemplate, times(2))
                .convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    void actualizarFormato_formatoNoEncontrado_lanzaExcepcion() {
        // Arrange
        when(formatoARepository.findById(999L)).thenReturn(Optional.empty());
        MockMultipartFile file = new MockMultipartFile("archivo", "f.pdf", "application/pdf", "x".getBytes());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                formatoAService.actualizarFormato(999L, file, "aprobado", "f.pdf", "2025-10-10 12:00:00"));

        assertTrue(ex.getMessage().contains("no encontrado"));
        verify(formatoARepository, never()).save(any());
    }

    @Test
    void actualizarFormato_masDeTresVersiones_lanzaExcepcion() {
        // Arrange
        formatoBase.setNroVersion(4);
        when(formatoARepository.findById(1L)).thenReturn(Optional.of(formatoBase));
        MockMultipartFile file = new MockMultipartFile("archivo", "f.pdf", "application/pdf", "x".getBytes());

        // Act & Assert
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                formatoAService.actualizarFormato(1L, file, "aprobado", "f.pdf", "2025-10-10 12:00:00"));

        assertEquals("No se pueden subir más de 3 versiones del Formato A para este proyecto.", ex.getMessage());
        verify(formatoARepository, never()).save(any());
    }
}
