package xyz.housailei.backend.agent.model.message;

import lombok.Data;

@Data
public class MessageFile {
    private String type;
    private String url;
    private Long expireAt;

    public MessageFile() {
    }

    public MessageFile(String type, String url, Long expireAt) {
        this.type = type;
        this.url = url;
        this.expireAt = expireAt;
    }

    public String toString() {
        return "MessageFile{, type='" + this.type + '\'' + ", url='" + this.url + '\'' + ", expireAt=" + this.expireAt + '}';
    }

    public static class Type {
        public static final String IMAGE = "IMAGE";
        public static final String AUDIO = "AUDIO";
        public static final String VIDEO = "VIDEO";
        public static final String FILE = "FILE";

        public Type() {
        }
    }
}
