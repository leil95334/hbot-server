package xyz.housailei.backend.agent.model.response;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class SyncCompletionResponse {
    private String requestId;
    private List<Answer> result;

    public SyncCompletionResponse() {
    }

    public static class Answer {
        private Object extraParams;
        private String mediaType;
        private String chunk;

        public Answer() {
        }
    }
}
