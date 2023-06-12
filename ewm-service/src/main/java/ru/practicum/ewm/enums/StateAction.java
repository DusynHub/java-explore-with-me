package ru.practicum.ewm.enums;

public enum StateAction {

        PUBLISH_EVENT,
        REJECT_EVENT,
        CANCEL_REVIEW,
        SEND_TO_REVIEW;

        public static StateAction getStateAction(String potentialStateAction) {
                try {
                        return StateAction.valueOf(potentialStateAction);
                } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATE_ACTION");
                }
        }
}
