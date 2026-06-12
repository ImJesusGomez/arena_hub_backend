package com.jesus_dev.arena_hub.mail.dto;

public record EmailRequestDTO (
        String to,
        String subject,
        String bodyText,
        String callToActionUrl,
        String callToActionLabel
) {
}
