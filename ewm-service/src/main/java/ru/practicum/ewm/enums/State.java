package ru.practicum.ewm.enums;

public enum State {

    PENDING,
    PUBLISHED,
    CANCELED;

    public static State getStateFromStateAction(StateAction stateAction) {
        if (stateAction == StateAction.REJECT_EVENT) {
            return State.PUBLISHED;
        } else {
            return State.CANCELED;
        }
    }
}
