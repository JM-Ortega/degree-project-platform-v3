package co.edu.unicauca.notificationservice.sender;

import co.edu.unicauca.notificationservice.service.InformationService;
import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Decorador de {@link NotificationSender} que agrega funcionalidad
 * para el envío de mensajes SMS, además del envío base (correo electrónico).
 * <p>
 * Implementa el patrón de diseño <b>Decorator</b>, permitiendo extender el
 * comportamiento del componente sin modificar su estructura interna.
 */
@Slf4j
public class SmsNotificationDecorator implements NotificationSender {
    private final InformationService informationService;

    /**
     * Componente base decorado (por ejemplo, {@link EmailNotificationSender}).
     */
    private final NotificationSender wrapped;

    /**
     * Constructor que recibe el componente base a decorar.
     *
     * @param wrapped instancia del {@link NotificationSender} base.
     */
    public SmsNotificationDecorator(NotificationSender wrapped, InformationService informationService) {
        this.wrapped = wrapped;
        this.informationService = informationService;
    }

    /**
     * Envía una notificación combinando correo electrónico y SMS.
     * <ul>
     *     <li>Primero envía la notificación base (correo electrónico).</li>
     *     <li>Luego, si existen números telefónicos, envía un SMS a cada uno.</li>
     * </ul>
     *
     * @param event evento de notificación con los datos del mensaje y destinatarios.
     */
    @Override
    public void send(NotificationEvent event) {
        // Envío base (correo electrónico)
        wrapped.send(event);

        for(String correo : event.getCorreos()){
            String celular = informationService.getTelefono(correo);
            if (celular == null){
                log.info("""
                    
                    📱 No es posible enviar un SMS
                    └── El destinatario no ha registrado su número de telefono
                    """);
            }else {
                log.info("""
                    
                    📱 Enviando SMS
                    ├── A: {}
                    └── Mensaje: {}
                    """, celular, event.getMensaje());
            }
        }
    }
}
