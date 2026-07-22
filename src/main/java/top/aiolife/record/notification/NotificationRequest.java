package top.aiolife.record.notification;

public record NotificationRequest(
        Long receiverUserId,
        String bizType,
        String title,
        String textContent,
        String actionUrl,
        String dedupKey
) {
}
