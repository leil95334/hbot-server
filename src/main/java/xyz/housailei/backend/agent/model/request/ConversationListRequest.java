package xyz.housailei.backend.agent.model.request;

import lombok.Data;

@Data
public class ConversationListRequest {
    private String appId;
    private String userId;
    private String source;
    private Integer pageNum;
    private Integer pageSize;
    private String sortOrder;

    public ConversationListRequest() {
    }

    public ConversationListRequest(String appId) {
        this.appId = appId;
    }

    public ConversationListRequest(String appId, String userId, String source, Integer pageNum, Integer pageSize, String sortOrder) {
        this.appId = appId;
        this.userId = userId;
        this.source = source;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.sortOrder = sortOrder;
    }

    public String toString() {
        return "ConversationListRequest{appId='" + this.appId + '\'' + ", userId='" + this.userId + '\'' + ", source='" + this.source + '\'' + ", pageNum=" + this.pageNum + ", pageSize=" + this.pageSize + ", sortOrder='" + this.sortOrder + '\'' + '}';
    }

    public static class Source {
        public static final String AGENT_SDK = "AGENT_SDK";
        public static final String OPENAPI = "OPENAPI";
        public static final String IOT_DEVICE = "IOT_DEVICE";

        public Source() {
        }
    }

    public static class SortOrder {
        public static final String ASC = "ASC";
        public static final String DESC = "DESC";

        public SortOrder() {
        }
    }
}
