package co.edu.unicauca.notificationservice.config;

import co.edu.unicauca.notificationservice.sender.EmailNotificationSender;
import co.edu.unicauca.notificationservice.sender.NotificationSender;
import co.edu.unicauca.notificationservice.sender.SmsNotificationDecorator;
import co.edu.unicauca.notificationservice.service.InformationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfig {

    /**
     * Bean decorado: correo + SMS.
     */
    @Bean("smsNotificationSender")
    public NotificationSender smsNotificationSender(EmailNotificationSender email, InformationService informationService) {
        return new SmsNotificationDecorator(email, informationService);
    }
}
