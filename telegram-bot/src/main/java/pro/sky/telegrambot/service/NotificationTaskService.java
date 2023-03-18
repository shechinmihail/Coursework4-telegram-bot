package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificationTaskService {

    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskService(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @Transactional
    public void saveNotificationTask(NotificationTask notificationTask) {
                notificationTaskRepository.save(notificationTask);
    }

    public List<NotificationTask> findNotificationsForSend() {
        return notificationTaskRepository.findNotificationTaskByDateTimeSend(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    }

    @Transactional
    public void deleteTask(NotificationTask notificationTask) {
        notificationTaskRepository.delete(notificationTask);
    }
}
