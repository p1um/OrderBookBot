package ru.bondholders.telegram.bot.handler;

import ru.bondholders.telegram.bot.State;
import ru.bondholders.telegram.model.User;
import ru.bondholders.telegram.repository.JpaUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.Serializable;
import java.util.List;

import static ru.bondholders.telegram.util.TelegramUtil.createMessageTemplate;


public class OrderRegistrationHandler implements Handler {

    public static final String ORDER_REGISTRATION = "/order_registration";

    @Value("${bot.name}")
    private String botUsername;

    private final JpaUserRepository userRepository;

    public OrderRegistrationHandler(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        // Welcoming user

        user.setName(message);
        userRepository.save(user);

        SendMessage welcomeMessage = createMessageTemplate(user)
                .setText("Заполним информацию о заявке");
        // Asking to input name

        SendMessage registrationMessage = createMessageTemplate(user)
                .setText("Укажи свое ФИО в формате: Иванов Петр Иванович");
        // Changing user state to "awaiting for name input"
//        user.setBotState(State.ENTER_NAME);
//        userRepository.save(user);

        return List.of(welcomeMessage, registrationMessage);

    }


    @Override
    public State operatedBotState() {
        return State.ORDER_REGISTRATION;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(ORDER_REGISTRATION);
    }
}
