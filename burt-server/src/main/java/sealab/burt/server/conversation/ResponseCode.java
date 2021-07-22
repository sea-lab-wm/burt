package sealab.burt.server.conversation;

public enum ResponseCode {
    SUCCESS(0), END_CONVERSATION(100), UNEXPECTED_ERROR (-1), NO_INFO_FOR_REPORT(-2);

    private final int value;

    ResponseCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
