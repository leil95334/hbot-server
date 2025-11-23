package xyz.housailei.backend.agent.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.housailei.backend.agent.model.file.File;
import xyz.housailei.backend.agent.model.message.MessageParser;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompletionRequest {
    private String appId;
    private String userId;
    private Map<String, Object> inputs = null;
    private String requestId = null;
    private MessageParser messageParser = null;
    private List<File> files = null;
    private Boolean stream = true;
    private Map<String, Object> clientProperties = null;

    public CompletionRequest(String appId, String userId) {
        this.appId = appId;
        this.userId = userId;
    }

    public CompletionRequest(String appId, String userId, Map<String, Object> inputs) {
        this.appId = appId;
        this.userId = userId;
        this.inputs = inputs;
    }

}
