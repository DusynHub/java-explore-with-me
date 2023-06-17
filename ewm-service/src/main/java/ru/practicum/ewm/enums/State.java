package ru.practicum.ewm.enums;

public enum State {

    PENDING,
    PUBLISHED,
    CANCELED;

    public static State getStateFromStateAction(String stateAction) {
        if (stateAction.equals("PUBLISH_EVENT")) {
            return State.PUBLISHED;
        } else if (stateAction.equals("SEND_TO_REVIEW")) {
            return State.PENDING;
        } else {
            return State.CANCELED;
        }
    }
}
