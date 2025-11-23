package xyz.housailei.backend.agent.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageListRequest {
    private String conversationId;
    private Integer pageNum;
    private Integer pageSize;
    private String sortOrder;

    public MessageListRequest(String conversationId) {
        this.conversationId = conversationId;
    }


    public String toString() {
        return "MessageListRequest{conversationId='" + this.conversationId + '\'' + ", pageNum=" + this.pageNum + ", pageSize=" + this.pageSize + ", sortOrder='" + this.sortOrder + '\'' + '}';
    }

    public static class SortOrder {
        public static final String ASC = "ASC";
        public static final String DESC = "DESC";

        public SortOrder() {
        }
    }
}
