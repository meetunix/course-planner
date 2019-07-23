package de.uni.swt.spring.app.mail;

public interface EmailService {
    void sendSimpleMessage(String from,
                           String to,
                           String subject,
                           String text);
}
