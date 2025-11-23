package xyz.housailei.backend.agent.model.response;

import lombok.Getter;

@Getter
public enum MediaType {
    TEXT("text", "文本"),
    IMAGE("image", "图片"),
    AUDIO("audio", "音频"),
    VIDEO("video", "视频"),
    FILE("file", "文件");

    private final String value;
    private final String description;

    private MediaType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static MediaType fromValue(String value) {
        MediaType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            MediaType type = var1[var3];
            if (type.value.equals(value)) {
                return type;
            }
        }

        return null;
    }

    public String toString() {
        return this.value;
    }
}
