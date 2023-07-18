package features.topic.utils;

public enum MessageDirection {
    INBOUND0("in-0"),
    INBOUND1("in-1"),
    OUTBOUND0("out-0"),
    OUTBOUND1("out-1")
    ;

    private final String direction;

    MessageDirection(String direction) {
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }

    public static MessageDirection fromString(String lowerCaseValue) {
        for (MessageDirection color : MessageDirection.values()) {
            if (color.getDirection().toLowerCase().equals(lowerCaseValue)) {
                return color;
            }
        }
        throw new IllegalArgumentException("Invalid TestDirection string: " + lowerCaseValue);
    }

}
