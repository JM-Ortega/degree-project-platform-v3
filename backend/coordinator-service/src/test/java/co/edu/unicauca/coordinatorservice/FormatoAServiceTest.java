package co.edu.unicauca.coordinatorservice;

import co.edu.unicauca.coordinatorservice.entity.Coordinador;
import co.edu.unicauca.coordinatorservice.entity.Docente;
import co.edu.unicauca.coordinatorservice.entity.Estudiante;
import co.edu.unicauca.coordinatorservice.entity.FormatoA;
import co.edu.unicauca.coordinatorservice.repository.CoordinadorRepository;
import co.edu.unicauca.coordinatorservice.repository.FormatoARepository;
import co.edu.unicauca.coordinatorservice.service.FormatoAService;
import co.edu.unicauca.shared.contracts.events.academic.DTOs.FormatoADTO;
import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;
import co.edu.unicauca.shared.contracts.model.Programa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.*;

import org.springframework.amqp.support.converter.MessageConverter;
import static co.edu.unicauca.shared.contracts.messaging.RoutingKeys.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class FormatoAServiceTest {

    private FormatoARepository formatoARepository;
    private RabbitTemplate rabbitTemplate;
    private CoordinadorRepository coordinadorRepository;

    private FormatoAService formatoAService;

    @BeforeEach
    void setup() {
        formatoARepository = mock(FormatoARepository.class);
        rabbitTemplate = mock(RabbitTemplate.class);
        coordinadorRepository = mock(CoordinadorRepository.class);

        formatoAService = new FormatoAService(
                formatoARepository, rabbitTemplate, coordinadorRepository
        );

        // Necesario para que el exchange no sea null
        TestUtils.injectValue(formatoAService, "mainExchange", "main.exchange");

        MessageConverter converter = mock(MessageConverter.class);
        when(rabbitTemplate.getMessageConverter()).thenReturn(converter);
    }

    private FormatoA crearFormatoBase() {
        FormatoA formato = new FormatoA();
        formato.setId(UUID.randomUUID());
        formato.setProyectoId(UUID.randomUUID());
        formato.setNombreProyecto("Proyecto de Prueba");
        formato.setNroVersion(1);

        // Director
        Docente director = new Docente();
        director.setEmail("director@correo.com");
        formato.setDirector(director);

        // Codirector
        Docente codirector = new Docente();
        codirector.setEmail("codirector@correo.com");
        formato.setCoodirector(codirector);

        // Estudiantes
        Estudiante est = new Estudiante();
        est.setEmail("est1@correo.com");
        est.setPrograma(Programa.INGENIERIA_DE_SISTEMAS);
        formato.setEstudiantes(List.of(est));

        return formato;
    }

    @Test
    void actualizarFormato_formatoNoExiste_lanzaExcepcion() {
        UUID id = UUID.randomUUID();

        when(formatoARepository.findById(id))
                .thenReturn(Optional.empty());

        MockMultipartFile archivo = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", "contenido".getBytes()
        );

        assertThrows(IllegalArgumentException.class, () ->
                formatoAService.actualizarFormato(id, archivo, "aprobado", "file.pdf")
        );
    }

    @Test
    void actualizarFormato_tieneMasDe3Versiones_lanzaExcepcion() {
        FormatoA formato = crearFormatoBase();
        formato.setNroVersion(4);

        when(formatoARepository.findById(formato.getId()))
                .thenReturn(Optional.of(formato));

        MockMultipartFile archivo = new MockMultipartFile(
                "file", "a.pdf", "application/pdf", "abc".getBytes()
        );

        assertThrows(IllegalStateException.class, () ->
                formatoAService.actualizarFormato(
                        formato.getId(), archivo, "aprobado", "a.pdf"
                )
        );
    }

    @Test
    void actualizarFormato_aprobado_enviaEventoYNotificacion() throws IOException {
        FormatoA formato = crearFormatoBase();

        when(formatoARepository.findById(formato.getId()))
                .thenReturn(Optional.of(formato));

        MockMultipartFile archivo = new MockMultipartFile(
                "file", "file.pdf", "application/pdf", "contenido".getBytes()
        );

        // Ejecutar
        FormatoA actualizado = formatoAService.actualizarFormato(
                formato.getId(), archivo, "aprobado", "file.pdf"
        );

        // Verifica guardado
        verify(formatoARepository).save(formato);

        assertEquals("file.pdf", actualizado.getNombreFormatoA());
        assertEquals(EstadoFormatoA.APROBADO, actualizado.getEstadoFormatoA());

        // Capturar evento publicado funcional
        ArgumentCaptor<FormatoADTO> dtoCaptor = ArgumentCaptor.forClass(FormatoADTO.class);

        verify(rabbitTemplate).convertAndSend(
                eq("main.exchange"),
                eq(COORDINATOR_FORMAT_A_APPROVED),
                dtoCaptor.capture()
        );

        FormatoADTO dto = dtoCaptor.getValue();
        assertEquals(formato.getId(), dto.getId());
        assertEquals(formato.getNroVersion(), dto.getNroVersion());

        // Capturar evento de notificación
        ArgumentCaptor<NotificationEvent> notiCaptor = ArgumentCaptor.forClass(NotificationEvent.class);

        verify(rabbitTemplate).convertAndSend(
                eq("main.exchange"),
                eq(NOTIFICATION_SEND + "." + COORDINATOR_FORMAT_A_APPROVED),
                notiCaptor.capture()
        );

        NotificationEvent ne = notiCaptor.getValue();

        assertEquals("Formato A Aprobado", ne.getAsunto());
        assertEquals("El Formato A del proyecto 'Proyecto de Prueba' fue APROBADO por el coordinador.", ne.getMensaje());
        assertEquals(3, ne.getCorreos().size());
    }

    @Test
    void actualizarFormato_rechazado_enviaEventoYNotificacionRechazo() throws IOException {
        FormatoA formato = crearFormatoBase();

        when(formatoARepository.findById(formato.getId()))
                .thenReturn(Optional.of(formato));

        MockMultipartFile archivo = new MockMultipartFile(
                "file", "file.pdf", "application/pdf", "contenido".getBytes()
        );

        formatoAService.actualizarFormato(
                formato.getId(), archivo, "rechazado", "file.pdf"
        );

        // Capturar notificación
        ArgumentCaptor<NotificationEvent> captor = ArgumentCaptor.forClass(NotificationEvent.class);

        verify(rabbitTemplate).convertAndSend(
                eq("main.exchange"),
                eq(NOTIFICATION_SEND + "." + COORDINATOR_FORMAT_A_REJECTED),
                captor.capture()
        );

        NotificationEvent ne = captor.getValue();

        assertEquals("Formato A Rechazado", ne.getAsunto());
        assertTrue(ne.getMensaje().contains("RECHAZADO"));
    }

    // ===============================================================
    // Tests de listarResumenPorCoordinador
    // ===============================================================

    @Test
    void listarResumenPorCoordinador_noExisteCoordinador_lanzaExcepcion() {
        when(coordinadorRepository.findByCorreo("correo@x.com"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                formatoAService.listarResumenPorCoordinador("correo@x.com")
        );
    }

    @Test
    void listarResumenPorCoordinador_filtraPorPrograma() {
        Coordinador coord = new Coordinador();
        coord.setPrograma(Programa.INGENIERIA_DE_SISTEMAS);

        when(coordinadorRepository.findByCorreo("coord@x.com"))
                .thenReturn(Optional.of(coord));

        // Formato que sí coincide
        FormatoA f1 = crearFormatoBase();

        // Formato que NO coincide
        FormatoA f2 = crearFormatoBase();
        f2.getEstudiantes().get(0).setPrograma(Programa.AUTOMATICA_INDUSTRIAL);

        when(formatoARepository.findAll())
                .thenReturn(List.of(f1, f2));

        var res = formatoAService.listarResumenPorCoordinador("coord@x.com");

        assertEquals(1, res.size());
        assertEquals(f1.getId(), res.get(0).getId());
    }
}