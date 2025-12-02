package co.edu.unicauca.notificationservice;

import co.edu.unicauca.notificationservice.sender.NotificationSender;
import co.edu.unicauca.notificationservice.service.InformationService;
import co.edu.unicauca.notificationservice.service.NotificationService;
import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import co.edu.unicauca.shared.contracts.model.Departamento;
import co.edu.unicauca.shared.contracts.model.Programa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private NotificationSender emailSender;
    private NotificationSender smsSender;
    private InformationService informationService;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        emailSender = mock(NotificationSender.class);
        smsSender = mock(NotificationSender.class);
        informationService = mock(InformationService.class);

        notificationService = new NotificationService(emailSender, smsSender, informationService);
    }

    private NotificationEvent buildEvent() {
        NotificationEvent event = mock(NotificationEvent.class);

        when(event.getCorreos()).thenReturn(new ArrayList<>());
        when(event.getPrograma()).thenReturn(Programa.INGENIERIA_DE_SISTEMAS);
        when(event.getDepartamento()).thenReturn(Departamento.SISTEMAS);
        when(event.getMensaje()).thenReturn("Mensaje");
        when(event.getTipo()).thenReturn("coordinador");
        when(event.isSMS()).thenReturn(false);

        return event;
    }

    @Test
    void notificar_enviaCorreoCuandoNoEsSMS() {
        NotificationEvent event = buildEvent();

        notificationService.notificar(event);

        verify(emailSender, times(1)).send(event);
        verifyNoInteractions(smsSender);
    }

    @Test
    void notificar_enviaSmsCuandoEsSMS() {
        NotificationEvent event = buildEvent();
        when(event.isSMS()).thenReturn(true);
        when(event.getCorreos()).thenReturn(List.of("a@unicauca.edu.co"));

        when(informationService.getTelefono("a@unicauca.edu.co")).thenReturn("3111111111");

        notificationService.notificar(event);

        verify(smsSender, times(1)).send(event);
        verifyNoInteractions(emailSender);
    }

    @Test
    void agregarDestinatarios_coordinadorExistente_seAgregaCorreo() {
        NotificationEvent event = buildEvent();
        List<String> correos = new ArrayList<>();
        when(event.getCorreos()).thenReturn(correos);
        when(event.getTipo()).thenReturn("coordinador");

        when(informationService.getEmailCoordinador("INGENIERIA_DE_SISTEMAS"))
                .thenReturn("coord@unicauca.edu.co");

        notificationService.notificar(event);

        assertTrue(correos.contains("coord@unicauca.edu.co"));
    }

    @Test
    void agregarDestinatarios_coordinadorNoExiste_muestraAdvertenciaPeroNoRevienta() {
        NotificationEvent event = buildEvent();
        when(event.getCorreos()).thenReturn(new ArrayList<>());
        when(event.getTipo()).thenReturn("coordinador");

        when(informationService.getEmailCoordinador("INGENIERIA_DE_SISTEMAS"))
                .thenReturn(null);

        assertDoesNotThrow(() -> notificationService.notificar(event));
    }

    @Test
    void agregarDestinatarios_jefeDepartamentoExistente_seAgregaCorreo() {
        NotificationEvent event = buildEvent();
        when(event.getTipo()).thenReturn("anteproyecto.created");
        List<String> correos = new ArrayList<>();
        when(event.getCorreos()).thenReturn(correos);

        when(informationService.getEmailJefeDepartamento("SISTEMAS"))
                .thenReturn("jefe@unicauca.edu.co");

        notificationService.notificar(event);

        assertTrue(correos.contains("jefe@unicauca.edu.co"));
    }

    @Test
    void enviarSms_sinTelefonos_noEnvia() {
        NotificationEvent event = buildEvent();
        when(event.isSMS()).thenReturn(true);
        when(event.getCorreos()).thenReturn(List.of("a@unicauca.edu.co"));

        when(informationService.getTelefono("a@unicauca.edu.co")).thenReturn(null);

        notificationService.notificar(event);

        verify(smsSender, never()).send(any());
    }

    @Test
    void enviarSms_conTelefonos_seteaTelefonosYEnvia() {
        NotificationEvent event = new NotificationEvent();
        event.setTipo("coordinador");
        event.setPrograma(Programa.INGENIERIA_DE_SISTEMAS);
        event.setDepartamento(Departamento.SISTEMAS);
        event.setMensaje("Mensaje de prueba");
        event.setCorreos(new ArrayList<>(List.of(
                "a@unicauca.edu.co",
                "c@unicauca.edu.co"
        )));
        event.setSMS(true);

        when(informationService.getTelefono("a@unicauca.edu.co")).thenReturn("3000000000");
        when(informationService.getTelefono("c@unicauca.edu.co")).thenReturn(null);

        when(informationService.getEmailCoordinador("INGENIERIA_DE_SISTEMAS"))
                .thenReturn(null);

        ArgumentCaptor<NotificationEvent> captor = ArgumentCaptor.forClass(NotificationEvent.class);

        notificationService.notificar(event);

        verify(smsSender).send(captor.capture());
        NotificationEvent enviado = captor.getValue();

        assertNotNull(enviado);
        assertNotNull(enviado.getTelefonos());
        assertEquals(1, enviado.getTelefonos().size());
        assertEquals("3000000000", enviado.getTelefonos().get(0));
    }
}
