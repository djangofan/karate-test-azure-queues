package com.test.functions.model.enums;

public enum DocumentStatusEnum {
    STARTED("Started"),
    PROCESSING("Processing"),
    DELETED("Deleted");

    public final String metaDocumentStatus;

    DocumentStatusEnum(String metaDocumentStatus) {
        this.metaDocumentStatus = metaDocumentStatus;
    }

    public String getMetaDocumentStatus() {
        return this.metaDocumentStatus;
    }
}
