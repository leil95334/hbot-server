package xyz.housailei.backend.agent.model.request;

import lombok.Data;
import xyz.housailei.backend.agent.model.file.File;
import xyz.housailei.backend.agent.model.message.MessageParser;

import java.util.List;
import java.util.Map;

@Data
public class ChatRequest {
    private String appId;
    private String query;
    private String userId;
    private String conversationId = null;
    private String requestId = null;
    private Map<String, Object> clientProperties = null;
    private List<File> files = null;
    private Boolean stream = true;
    private Boolean searchEngine = false;
    private MessageParser messageParser = null;

    public ChatRequest(String appId, String query, String userId) {
        this.appId = appId;
        this.query = query;
        this.userId = userId;
    }

    public ChatRequest(String appId, String query, String userId, String conversationId) {
        this.appId = appId;
        this.query = query;
        this.userId = userId;
        this.conversationId = conversationId;
    }

    public ChatRequest(String appId, String query, String userId, String conversationId, String requestId, Map<String, Object> clientProperties, List<File> files, boolean stream, MessageParser messageParser) {
        this.appId = appId;
        this.query = query;
        this.userId = userId;
        this.conversationId = conversationId;
        this.requestId = requestId;
        this.clientProperties = clientProperties;
        this.files = files;
        this.stream = stream;
        this.messageParser = messageParser;
    }
}
