package cz.skalicky.emailtracking.config;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import cz.skalicky.emailtracking.businesslogic.MailSenderBean;

@Configuration
@PropertySource("classpath:mail.properties")
public class MailConfig {

    private static class Properties {
        private static final String SENDER_EMAIL = "mail.sender.email";
        private static final String SENDER_USERNAME = "mail.sender.username";
        private static final String SENDER_PASSWORD = "mail.sender.password";
        private static final String RECIPIENT_EMAIL = "mail.recipient.email";
    }

    @Inject
    private Environment environment;

    @Bean
    public MailSenderBean mailSenderBean() {

        return new MailSenderBean(environment.getRequiredProperty(Properties.SENDER_EMAIL),
                environment.getRequiredProperty(Properties.SENDER_USERNAME),
                environment.getRequiredProperty(Properties.SENDER_PASSWORD),
                environment.getProperty(Properties.RECIPIENT_EMAIL));
    }

}
