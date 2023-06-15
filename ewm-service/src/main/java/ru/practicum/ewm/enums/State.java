package ru.practicum.ewm.enums;

public enum State {

    PENDING,
    PUBLISHED,
    CANCELED;

    public static State getStateFromStateAction(StateAction stateAction) {
        if (stateAction == StateAction.PUBLISH_EVENT) {
            return State.PUBLISHED;
        } else if(stateAction == StateAction.SEND_TO_REVIEW) {
            return State.PENDING;
        } else {
            return State.CANCELED;
        }
    }
}
