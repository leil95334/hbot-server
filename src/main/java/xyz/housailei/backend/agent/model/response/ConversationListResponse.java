package xyz.housailei.backend.agent.model.response;

import lombok.Getter;
import xyz.housailei.backend.agent.model.Conversation.Conversation;

import java.util.List;

@Getter
public class ConversationListResponse {
    private List<Conversation> conversations;
    private Integer currentPage;
    private Integer pageSize;
    private Integer total;

    public ConversationListResponse() {
    }

}
