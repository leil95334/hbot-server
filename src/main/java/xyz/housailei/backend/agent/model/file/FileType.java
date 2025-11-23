package xyz.housailei.backend.agent.model.file;

import lombok.Getter;

@Getter
public enum FileType {
    IMAGE("IMAGE", "Image file"),
    AUDIO("AUDIO", "Audio file"),
    VIDEO("VIDEO", "Video file"),
    FILE("FILE", "General file");

    private final String value;
    private final String description;

    private FileType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static FileType fromValue(String value) {
        FileType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            FileType type = var1[var3];
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
