package ru.bondholders.telegram.bot;

// Available bot states
public enum State {
    NONE,
    START,
    ORDER,
    ORDER_REGISTRATION,
    ENTER_NAME,
    FILL_ORDER,
    FILL_ORDER_BROK,
    PLAYING_QUIZ,
}
