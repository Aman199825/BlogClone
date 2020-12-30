package com.example.demo.service;

import com.example.demo.domain.NotificationEmail;
import com.example.demo.exceptions.SpringRedditException;
import lombok.AllArgsConstructor;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;




@Service
@AllArgsConstructor

@Slf4j
public class MailService {

    private final JavaMailSender mailSender;
    private final MailContentBuilder mailContentBuilder;
    @Async
    public void sendEmail(NotificationEmail notificationEmail)
    {
        MimeMessagePreparator mimeMessagePreparator=mimeMessage -> {
            MimeMessageHelper messageHelper=new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("aman@email.com");
            messageHelper.setTo(notificationEmail.getRecipient());
            messageHelper.setSubject(notificationEmail.getSubject());
            messageHelper.setText(notificationEmail.getBody());
        };
        try {
            mailSender.send(mimeMessagePreparator);
            log.info("activation mail sent");
        }
        catch(MailException e)
        {
            throw new SpringRedditException("exception occured when sending mail to "+notificationEmail.getRecipient());
        }
    }
}
