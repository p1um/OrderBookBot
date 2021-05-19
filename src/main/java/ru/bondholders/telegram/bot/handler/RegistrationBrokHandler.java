package ru.bondholders.telegram.bot.handler;

import ru.bondholders.telegram.bot.State;
import ru.bondholders.telegram.model.User;
import ru.bondholders.telegram.repository.JpaUserRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static ru.bondholders.telegram.util.TelegramUtil.createInlineKeyboardButton;
import static ru.bondholders.telegram.util.TelegramUtil.createMessageTemplate;

@Component
public class RegistrationBrokHandler implements Handler {
    // Supported CallBackQueries are stored as constants
    public static final String NAME_ACCEPT = "/enter_name_accept";
    public static final String NAME_CHANGE = "/enter_name";
    public static final String NAME_CHANGE_CANCEL = "/enter_name_cancel";

    private final JpaUserRepository userRepository;

    public RegistrationBrokHandler(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        // Checking type of input message

        return checkName(user, message);

    }


    private List<PartialBotApiMethod<? extends Serializable>> checkName(User user, String message) {
        // When we check user name we store it in database immediately
        // refactoring idea: temporal storage
        user.setName(message);
        userRepository.save(user);

        // Creating button to accept changes
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                createInlineKeyboardButton("Accept", NAME_ACCEPT));

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        return List.of(createMessageTemplate(user)
                .setText(String.format("You have entered: %s%nIf this is correct - press the button", user.getName()))
                .setReplyMarkup(inlineKeyboardMarkup));
    }


    @Override
    public State operatedBotState() {
        return State.FILL_ORDER_BROK;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return Collections.emptyList();
    }
}
