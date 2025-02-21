package com.example.rbs.entity;

public enum BoxStatus {
    IN_USE("사용중"),
    AVAILABLE("사용가능"),
    BLOCKED("차단"),
    INSTALL_REQUEST("설치 요청"),
    INSTALL_IN_PROGRESS("설치 진행"),
    INSTALL_COMPLETED("설치 완료"),
    INSTALL_CONFIRMED("설치 확정"),
    REMOVE_REQUEST("제거 요청"),
    REMOVE_IN_PROGRESS("제거 진행"),
    REMOVE_COMPLETED("제거 완료"),
    REMOVE_CONFIRMED("제거 확정"),
    COLLECTION_NEEDED("수거 필요"),
    COLLECTION_RECOMMENDED("수거 권장");

    private final String description;

    BoxStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static BoxStatus fromString(String text) {
        for (BoxStatus status : BoxStatus.values()) {
            if (status.description.equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + text);
    }
}
