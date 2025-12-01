package co.edu.unicauca.notificationservice.config;

import co.edu.unicauca.notificationservice.sender.EmailNotificationSender;
import co.edu.unicauca.notificationservice.sender.NotificationSender;
import co.edu.unicauca.notificationservice.sender.SmsNotificationDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfig {

    /**
     * Bean decorado: correo + SMS.
     */
    @Bean("smsNotificationSender")
    public NotificationSender smsNotificationSender(EmailNotificationSender email) {
        return new SmsNotificationDecorator(email);
    }
}
