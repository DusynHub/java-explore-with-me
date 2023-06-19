package ru.practicum.ewm.enums;

public enum StateAction {

    PUBLISH_EVENT(State.PUBLISHED),
    REJECT_EVENT(State.CANCELED),
    CANCEL_REVIEW(State.CANCELED),
    SEND_TO_REVIEW(State.PENDING);

    private final State state;

    StateAction(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public static StateAction getStateAction(String potentialStateAction) {
        try {
            return StateAction.valueOf(potentialStateAction);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATE_ACTION");
        }
    }
}
