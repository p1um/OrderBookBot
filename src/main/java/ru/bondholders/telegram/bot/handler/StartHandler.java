package ru.bondholders.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.bondholders.telegram.bot.State;
import ru.bondholders.telegram.model.User;
import ru.bondholders.telegram.repository.JpaUserRepository;

import java.io.Serializable;
import java.util.List;

import static ru.bondholders.telegram.util.TelegramUtil.createInlineKeyboardButton;
import static ru.bondholders.telegram.util.TelegramUtil.createMessageTemplate;

@Component
public class StartHandler implements Handler {

    public static final String START = "/start";

    //TODO обработать /start для существующих пользователей - вызывать меню /help

    @Value("${bot.name}")
    private String botUsername;

    private final JpaUserRepository userRepository;

    public StartHandler(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        // Welcoming user
        SendMessage welcomeMessage = createMessageTemplate(user)
                .setText(String.format(
                        "Привет это бот для участия в soft книге. Ты согласен на получение информации о текущих и будущих размещениях и использовании твоих данных для формирования заявки?", botUsername
                ));

        // Creating button to accept changes
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        //Текст и переход на следующее состояние
        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                createInlineKeyboardButton("Принять", OrderHandler.FIND_BOOK_ORDER));

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        //Меняем state
        user.setBotState(State.ORDER);
        userRepository.save(user);

        return List.of(welcomeMessage, createMessageTemplate(user)
                .setText(String.format("Если да, нажми кнопку ниже", user.getName()))
                .setReplyMarkup(inlineKeyboardMarkup));
    }

// State который может обрабатывать данный Handler
    @Override
    public State operatedBotState() {
        return State.START;
    }

    // Команды которые может обрабатывать данный хендлер
    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(START);
    }
}
