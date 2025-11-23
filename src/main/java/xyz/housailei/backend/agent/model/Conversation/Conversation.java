package xyz.housailei.backend.agent.model.Conversation;

import lombok.Data;

@Data
public class Conversation {
    private String conversationId;
    private String userId;
    private String source;
    private Long createAt;

    public Conversation() {
    }

    public Conversation(String conversationId, String userId, String source, Long createAt) {
        this.conversationId = conversationId;
        this.userId = userId;
        this.source = source;
        this.createAt = createAt;
    }

    public String toString() {
        return "Conversation{conversationId='" + this.conversationId + '\'' + ", userId='" + this.userId + '\'' + ", source='" + this.source + '\'' + ", createAt=" + this.createAt + '}';
    }

    public static class Source {
        public static final String AGENT_SDK = "AGENT_SDK";
        public static final String OPENAPI = "OPENAPI";
        public static final String IOT_DEVICE = "IOT_DEVICE";

        public Source() {
        }
    }
}
