
package com.pro.email.Service;


import com.pro.email.controller.ICSFileGenerator;
import com.pro.email.model.Event;
import com.pro.email.model.User;
import com.pro.email.repo.EventRepository;
import com.pro.email.repo.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    public Event createEvent(Event event) {
        // Save the event to the database
        Event savedEvent = eventRepository.save(event);

        // Get all users (Assuming you send the event to all users)
        List<User> users = userRepository.findAll();

        // Send emails to users
        for (User user : users) {
            try {
                sendEventEmail(user, savedEvent);
            } catch (MessagingException | IOException e) {
                System.err.println("Failed to send email to: " + user.getEmail());
                e.printStackTrace();
            }
        }

        return savedEvent;
    }

    private void sendEventEmail(User user, Event event) throws MessagingException, IOException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(user.getEmail());
        helper.setSubject("New Event: " + event.getTitle());

        // Generate .ics file content
        String icsContent = ICSFileGenerator.generateICSContent(event);

        // Attach .ics file
        InputStream is = new ByteArrayInputStream(icsContent.getBytes());
        helper.addAttachment("event.ics", new ByteArrayResource(is.readAllBytes()));

        // Email body
        String emailBody = "Hello " + user.getName() + ",\n\n" +
                "A new event has been created.\n\n" +
                "Event Details:\n" +
                "Title: " + event.getTitle() + "\n" +
                "Description: " + event.getDescription() + "\n" +
                "Location: " + event.getLocation() + "\n" +
                "Date: " + event.getEventDate() + "\n\n" +
                "Hope to see you there!\n";

        helper.setText(emailBody);

        mailSender.send(mimeMessage);
        System.out.println("Email sent to: " + user.getEmail());
    }

}
