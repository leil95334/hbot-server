package xyz.housailei.backend.agent.model.response;

import lombok.Getter;
import xyz.housailei.backend.agent.model.message.Message;

import java.util.List;

@Getter
public class MessageListResponse {
    private Integer currentPage;
    private Integer pageSize;
    private Integer total;
    private List<Message> messages;

    public MessageListResponse() {
    }

    public MessageListResponse(List<Message> messages) {
        this.messages = messages;
    }

    public String toString() {
        return "MessageListResponse{messages=" + this.messages + '}';
    }
}
