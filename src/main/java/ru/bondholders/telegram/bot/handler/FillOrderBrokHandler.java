package ru.bondholders.telegram.bot.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.bondholders.telegram.bot.State;
import ru.bondholders.telegram.model.User;
import ru.bondholders.telegram.repository.JpaUserRepository;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static ru.bondholders.telegram.util.TelegramUtil.createMessageTemplate;

@Component
public class FillOrderBrokHandler implements Handler {
    // Supported CallBackQueries are stored as constants

    public static final String FILL_ORDER_BROKER = "/fill_order_broker";

    private final JpaUserRepository userRepository;

    public FillOrderBrokHandler(JpaUserRepository userRepository) {
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
//        user.setName(message);
//        userRepository.save(user);

        SendMessage registrationMessage = createMessageTemplate(user)
                .setText("Укажи своего брокера (БКС, ПСБ и тд):");
        // Changing user state to "awaiting for name input"
        user.setBotState(State.FILL_ORDER_COUNT);
        userRepository.save(user);

        return List.of(registrationMessage);

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
