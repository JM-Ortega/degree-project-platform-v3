package co.edu.unicauca.notificationservice;

import co.edu.unicauca.notificationservice.consumer.NotificationListener;
import co.edu.unicauca.notificationservice.service.NotificationService;
import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class NotificationListenerTest {

    private NotificationService notificationService;
    private NotificationListener notificationListener;

    @BeforeEach
    void setUp() {
        notificationService = Mockito.mock(NotificationService.class);
        notificationListener = new NotificationListener(notificationService);
    }

    @Test
    void handleNotification_eventoNulo_noLanzaExcepcion() {
        notificationListener.handleNotification(null);
        verifyNoInteractions(notificationService);
    }

    @Test
    void handleNotification_eventoValido_invocaServicio() {
        NotificationEvent event = mock(NotificationEvent.class);

        when(event.getTipo()).thenReturn("test");
        when(event.getCorreos()).thenReturn(new ArrayList<>());
        when(event.getMensaje()).thenReturn("Mensaje de prueba");

        notificationListener.handleNotification(event);

        verify(notificationService, times(1)).notificar(event);
    }

    @Test
    void handleNotification_errorEnServicio_lanzaAmqpRejectException() {
        NotificationEvent event = mock(NotificationEvent.class);

        when(event.getTipo()).thenReturn("test");
        when(event.getCorreos()).thenReturn(new ArrayList<>());
        when(event.getMensaje()).thenReturn("Mensaje de prueba");

        doThrow(new RuntimeException("fallo interno"))
                .when(notificationService).notificar(event);

        assertThrows(
                AmqpRejectAndDontRequeueException.class,
                () -> notificationListener.handleNotification(event)
        );
    }
}
