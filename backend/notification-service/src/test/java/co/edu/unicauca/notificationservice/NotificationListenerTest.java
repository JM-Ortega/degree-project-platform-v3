package co.edu.unicauca.notificationservice;

import co.edu.unicauca.notificationservice.consumer.NotificationListener;
import co.edu.unicauca.notificationservice.sender.EmailNotificationSender;
import co.edu.unicauca.notificationservice.sender.NotificationSender;
import co.edu.unicauca.notificationservice.sender.SmsNotificationDecorator;
import co.edu.unicauca.notificationservice.service.InformationService;
import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

class NotificationListenerTest {

    private NotificationSender emailNotificationSender; // solo correo
    private NotificationSender smsNotificationSender;   // correo + SMS
    private NotificationListener listener;

    @BeforeEach
    void setup() {
        emailNotificationSender = mock(NotificationSender.class);
        smsNotificationSender = mock(NotificationSender.class);
        listener = new NotificationListener(emailNotificationSender, smsNotificationSender);
    }

    @Test
    void testHandleNotification_emailOnly_withEmptyPhones() {
        NotificationEvent event = new NotificationEvent();
        event.setType("coordinator.formata.approved");
        event.setRecipientEmails(List.of("test@correo.com"));
        event.setRecipientPhones(List.of()); // sin teléfonos
        event.setSubject("Formato A Aprobado");
        event.setMessage("El formato A fue aprobado correctamente.");
        event.setTimestamp(OffsetDateTime.now());

        listener.handleNotification(event);

        verify(emailNotificationSender, times(1)).send(event);
        verifyNoInteractions(smsNotificationSender);
    }

    @Test
    void testHandleNotification_emailOnly_withNullPhones() {
        NotificationEvent event = new NotificationEvent();
        event.setType("coordinator.formata.approved");
        event.setRecipientEmails(List.of("test@correo.com"));
        event.setRecipientPhones(null); // null explícito
        event.setSubject("Formato A Aprobado");
        event.setMessage("El formato A fue aprobado correctamente.");
        event.setTimestamp(OffsetDateTime.now());

        listener.handleNotification(event);

        verify(emailNotificationSender, times(1)).send(event);
        verifyNoInteractions(smsNotificationSender);
    }

    @Test
    void testHandleNotification_emailAndSms_whenPhonesPresent() {
        NotificationEvent event = new NotificationEvent();
        event.setType("coordinator.formata.rejected");
        event.setRecipientEmails(List.of("test@correo.com"));
        event.setRecipientPhones(List.of("3001234567")); // hay teléfono
        event.setSubject("Formato A Rechazado");
        event.setMessage("El formato A fue rechazado.");
        event.setTimestamp(OffsetDateTime.now());

        listener.handleNotification(event);

        verify(smsNotificationSender, times(1)).send(event);
        verifyNoInteractions(emailNotificationSender);
    }

    @Test
    void testNotificationDecoratorIntegration_delegatesToEmail() {
        NotificationSender emailSender = mock(EmailNotificationSender.class);
        InformationService informationService = mock(InformationService.class);
        NotificationSender smsSender = new SmsNotificationDecorator(emailSender, informationService);

        NotificationEvent event = new NotificationEvent();
        event.setType("test.sms");
        event.setRecipientEmails(List.of("test@correo.com"));
        event.setRecipientPhones(List.of("3001234567")); // fuerza el uso del decorador
        event.setSubject("Prueba SMS");
        event.setMessage("Mensaje de prueba de SMS");
        event.setTimestamp(OffsetDateTime.now());

        smsSender.send(event);

        ArgumentCaptor<NotificationEvent> captor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(emailSender, times(1)).send(captor.capture());
    }
}
