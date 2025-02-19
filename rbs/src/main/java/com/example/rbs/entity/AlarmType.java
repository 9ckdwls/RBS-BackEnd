package com.example.rbs.entity;

public enum AlarmType {
    FIRE("화재"),
    COLLECTION_NEEDED("수거 필요"),
    COLLECTION_RECOMMENDED("수거 권장"),
    INSTALL_REQUEST("설치 요청"),
    INSTALL_COMPLETED("설치 완료"),
    INSTALL_CONFIRMED("설치 확정"),
    REMOVE_REQUEST("제거 요청"),
    REMOVE_COMPLETED("제거 완료");

private final String description;

AlarmType(String description) {
    this.description = description;
}

public String getDescription() {
    return description;
}

public static AlarmType fromString(String text) {
    for (AlarmType b : AlarmType.values()) {
        if (b.description.equalsIgnoreCase(text)) {
            return b;
        }
    }
    throw new IllegalArgumentException("Unknown alarm type: " + text);
}
}