package com.jesus_dev.arena_hub.mail.service;

import com.jesus_dev.arena_hub.mail.dto.EmailRequestDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {


    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${app.mail.from-name}")
    private String fromName;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(EmailRequestDTO requestDTO) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(new InternetAddress(fromAddress, fromName));
            helper.setText(requestDTO.to());
            helper.setSubject(requestDTO.subject());

            String htmlContent = buidlHtmlContent(requestDTO);
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("The email could not be sent", e);
        }
    }

    private String buidlHtmlContent(EmailRequestDTO requestDTO) {
        String template = loadTemplate();

        String html = template
                .replace("{{appName}}", fromName)
                .replace("{{subject}}", requestDTO.subject())
                .replace("{{bodyText}}", requestDTO.bodyText());

        if (requestDTO.callToActionUrl() != null && !requestDTO.callToActionUrl().isBlank()) {
            String ctaBlock = """
                <div style="text-align:center; margin:32px 0;">
                  <a href="%s"
                     style="background-color:#1a56db; color:#ffffff; padding:12px 28px;
                            border-radius:6px; text-decoration:none; font-weight:600;
                            font-size:14px; display:inline-block;">
                    %s
                  </a>
                </div>
            """.formatted(requestDTO.callToActionUrl(), requestDTO.callToActionLabel());

            html = html.replace("{{#if callToActionUrl}}", "")
                    .replace("{{/if}}", "")
                    .replace("{{callToActionUrl}}", requestDTO.callToActionUrl())
                    .replace("{{callToActionLabel}}", requestDTO.callToActionLabel());
        } else {
            // Elimina el bloque del botón si no hay CTA
            html = html.replaceAll("(?s)\\{\\{#if callToActionUrl\\}\\}.*?\\{\\{/if\\}\\}", "");
        }

        return html;
    }


    private String loadTemplate() {
        try (InputStream is = getClass().getResourceAsStream("/templates/email-template.html")) {
            if (is == null) throw new RuntimeException("Template not found");
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Error reading email template", e);
        }
    }
}
