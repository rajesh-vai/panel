package search;

import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * Created by rvairamani on 23-05-2017.
 */

@Component
public class EmailComponent {

    @Bean
    public JavaMailSender javaMailService() {

        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setUsername("bhairavi.flipkart@gmail.com");
        javaMailSender.setPassword("Bhairavi#123");
        javaMailSender.setJavaMailProperties(props);
        return javaMailSender;
    }
}
