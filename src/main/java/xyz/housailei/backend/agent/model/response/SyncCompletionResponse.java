package xyz.housailei.backend.agent.model.response;

import lombok.Getter;

import java.util.List;

@Getter
public class SyncCompletionResponse {
    private String requestId;
    private List<Answer> result;

    public SyncCompletionResponse() {
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setResult(List<Answer> result) {
        this.result = result;
    }

    public static class Answer {
        private Object extraParams;
        private String mediaType;
        private String chunk;

        public Answer() {
        }
    }
}
