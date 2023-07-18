package com.test.functions.model.enums;

public enum DocumentEventType {
    PHASE1 ("PHASE1"),
    PHASE2 ("PHASE2");

    private final String eventType;

    DocumentEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventType() {
        return this.eventType;
    }

}
