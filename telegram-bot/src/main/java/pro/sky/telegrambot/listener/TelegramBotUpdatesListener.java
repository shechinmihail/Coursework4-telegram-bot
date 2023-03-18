package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private static final Pattern NOTIFICATION_TASK_PATTERN = Pattern.compile(
            "([\\d\\\\.:\\s]{16})(\\s)([А-яA-z\\s\\d,.!?;]+)");

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
            "dd.MM.yyyy HH:mm");

    private final TelegramBot telegramBot;
    private final NotificationTaskService notificationTaskService;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskService notificationTaskService) {
        this.telegramBot = telegramBot;
        this.notificationTaskService = notificationTaskService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        try {
            updates.stream().filter(update -> update.message() != null).
                    forEach(update -> {
                logger.info("Processing update: {}", update);
                Message message = update.message();
                String text = message.text();
                Long chatId = message.chat().id();
                if ("/start".equals(text)) {
                    sendMessage(chatId, "Привет! Для планирования задачи отправьте её в формате: 15.03.2023 11:00 Сделать домашнюю работу");
                } else if (text != null) {
                    Matcher matcher = NOTIFICATION_TASK_PATTERN.matcher(text);
                    if (matcher.find()) {
                        LocalDateTime localDateTime = parse(matcher.group(1));
                        if (Objects.isNull(localDateTime)) {
                            sendMessage(chatId, "Неверный формат! Правильный формат: 15.03.2023 20:00 Сделать домашнюю работу");
                        } else {
                            String text2 = matcher.group(3);
                            NotificationTask notificationTask = new NotificationTask();
                            notificationTask.setId(chatId);
                            notificationTask.setMessage(text2);
                            notificationTask.setDateTimeSend(localDateTime);
                            notificationTaskService.saveNotificationTask(notificationTask);
                            sendMessage(chatId,"Ваша задача запланирована!");
                        }
                    } else {
                        sendMessage(chatId, "Неверный формат сообщения!");
                    }
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        if (!sendResponse.isOk()) {
            logger.error("Все пропало!!!", sendResponse.description());
        }
    }

    @Nullable
    private LocalDateTime parse(String localDateTime) {
        try {
            return LocalDateTime.parse(localDateTime, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
