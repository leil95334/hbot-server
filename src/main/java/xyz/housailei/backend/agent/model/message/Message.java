package xyz.housailei.backend.agent.model.message;

import lombok.Data;
import lombok.Getter;
import xyz.housailei.backend.agent.model.response.MediaType;

import java.util.List;

@Data
public class Message {
    private String messageId;
    private String conversationId;
    private String appId;
    private String query;
    private List<Answer> answers;
    private List<MessageFile> files;
    private Long createAt;
    private Long updateAt;
    private String status;

    public Message() {
    }

    public Message(String messageId, String conversationId, String appId, String query, List<Answer> answers, List<MessageFile> files, Long createAt, Long updateAt, String status) {
        this.messageId = messageId;
        this.conversationId = conversationId;
        this.appId = appId;
        this.query = query;
        this.answers = answers;
        this.files = files;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.status = status;
    }

    public String toString() {
        return "Message{messageId='" + this.messageId + '\'' + ", conversationId='" + this.conversationId + '\'' + ", appId='" + this.appId + '\'' + ", query='" + this.query + '\'' + ", answers=" + this.answers + ", files=" + this.files + ", createAt=" + this.createAt + ", updateAt=" + this.updateAt + ", status='" + this.status + '\'' + '}';
    }

    public static class Status {
        public static final String SUCCESS = "SUCCESS";
        public static final String ERROR = "ERROR";
        public static final String BLOCK = "BLOCK";
        public static final String PENDING = "PENDING";

        public Status() {
        }
    }

    @Data
    public static class Answer {
        private String lane;
        private MediaType mediaType;
        private String text;
        private List<String> url;
        private Long expireAt;

        public Answer() {
        }

        public Answer(String lane, MediaType mediaType, String text, List<String> url, Long expireAt) {
            this.lane = lane;
            this.mediaType = mediaType;
            this.text = text;
            this.url = url;
            this.expireAt = expireAt;
        }
        public String toString() {
            return "Answer{lane='" + this.lane + '\'' + ", mediaType='" + this.mediaType + '\'' + ", text='" + this.text + '\'' + ", url=" + this.url + ", expireAt=" + this.expireAt + '}';
        }
    }
}
