package pro.sky.telegrambot.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_task")
public class NotificationTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "chat_id", nullable = false)
    private Long chatId;
    @Column(name = "message", nullable = false)
    private String message;
    @Column(name = "date_time_send", nullable = false)
    private LocalDateTime dateTimeSend;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDateTimeSend() {
        return dateTimeSend;
    }

    public void setDateTimeSend(LocalDateTime dateTimeSend) {
        this.dateTimeSend = dateTimeSend;
    }
}
