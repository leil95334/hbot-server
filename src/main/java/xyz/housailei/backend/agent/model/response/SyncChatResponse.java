package xyz.housailei.backend.agent.model.response;

import lombok.Data;

import java.util.List;

@Data
public class SyncChatResponse {
    private String requestId;
    private String conversationId;
    private String messageId;
    private List<Answer> result;

    public SyncChatResponse() {
    }

    public static class Answer {
        private String lane;
        private Object extraParams;
        private MediaType mediaType;
        private String chunk;

        public Answer() {
        }
    }
}
