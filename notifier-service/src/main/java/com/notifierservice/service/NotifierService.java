package com.notifierservice.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.notifierservice.event.BillEvent;
import com.notifierservice.event.RegistrationEvent;

@Service
public class NotifierService {

    @Autowired
    private JavaMailSender mailSender;

    @RabbitListener(queues = "registration.queue")
    public void handleRegistration(RegistrationEvent event) {
        sendMail(event.getEmail(), "Bienvenido " + event.getEmail(), "Gracias por registrarte.");
    }

    @RabbitListener(queues = "bill.queue")
    public void handleInvoice(BillEvent event) {
        String content = String.format("Factura por %.2f en %s el %s",
                event.getAmount(), event.getPointOfSaleName(), event.getDate());
        sendMail(event.getEmail(), "Factura de Acreditación", content);
    }

    private void sendMail(String to, String subject, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        mailSender.send(msg);
    }
}
