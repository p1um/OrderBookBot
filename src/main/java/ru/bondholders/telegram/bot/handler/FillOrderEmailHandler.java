package ru.bondholders.telegram.bot.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.bondholders.telegram.bot.State;
import ru.bondholders.telegram.model.User;
import ru.bondholders.telegram.repository.JpaUserRepository;

import java.io.Serializable;
import java.util.List;

import static ru.bondholders.telegram.util.TelegramUtil.createMessageTemplate;

@Component
public class FillOrderEmailHandler implements Handler {
    // Supported CallBackQueries are stored as constants
    public static final String FILL_ORDER_EMAIL = "/fill_order_email";

    private final JpaUserRepository userRepository;

    public FillOrderEmailHandler(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        // Checking type of input message

        return fillOrderFio(user, message);

    }


    private List<PartialBotApiMethod<? extends Serializable>> fillOrderFio(User user, String message) {

        SendMessage registrationMessage = createMessageTemplate(user)
                .setText("Укажи свою почту:");
        // Changing user state to "awaiting for name input"
        user.setBotState(State.FILL_ORDER_FINISH);
        userRepository.save(user);

        return List.of(registrationMessage);
    }


    @Override
    public State operatedBotState() {
        return State.FILL_ORDER_EMAIL;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(FILL_ORDER_EMAIL);
    }
}
