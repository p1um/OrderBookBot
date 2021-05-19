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

import static ru.bondholders.telegram.bot.handler.QuizHandler.FILL_ORDER;
import static ru.bondholders.telegram.util.TelegramUtil.createInlineKeyboardButton;
import static ru.bondholders.telegram.util.TelegramUtil.createMessageTemplate;

@Component
public class OrderHandler implements Handler {

    public static final String ORDER = "/order";

    @Value("${bot.name}")
    private String botUsername;

    private final JpaUserRepository userRepository;

    public OrderHandler(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {

        SendMessage welcomeMessage = createMessageTemplate(user)
                        .setText(String.format(
                "Новое размещение! Урожай-2, конь в наличии. Ставка - 12. Ежемесячный купон. Будешь участвовать?", botUsername
        ));

        // Creating button to accept changes
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                createInlineKeyboardButton("Принять", FILL_ORDER));

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        user.setBotState(State.ORDER_REGISTRATION);
        userRepository.save(user);

        return List.of(welcomeMessage, createMessageTemplate(user)
                .setText(String.format("Если да, нажми кнопку ниже", user.getName()))
                .setReplyMarkup(inlineKeyboardMarkup));
    }

    @Override
    public State operatedBotState() {
        return State.ORDER;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(ORDER);
    }
}
