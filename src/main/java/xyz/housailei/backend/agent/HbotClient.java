package xyz.housailei.backend.agent;


import com.alibaba.fastjson.TypeReference;
import xyz.housailei.backend.agent.core.exception.HbotClientConfigException;
import xyz.housailei.backend.agent.core.exception.HbotHttpResponseException;
import xyz.housailei.backend.agent.core.exception.HbotServerException;
import xyz.housailei.backend.agent.core.http.HttpClient;
import xyz.housailei.backend.agent.core.http.HttpClientConfig;
import xyz.housailei.backend.agent.core.http.HttpResponseEvent;
import xyz.housailei.backend.agent.model.message.MessageParser;
import xyz.housailei.backend.agent.model.request.ChatRequest;
import xyz.housailei.backend.agent.model.request.CompletionRequest;
import xyz.housailei.backend.agent.model.request.ConversationListRequest;
import xyz.housailei.backend.agent.model.request.MessageListRequest;
import xyz.housailei.backend.agent.model.response.*;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class HbotClient {
    private final HttpClientConfig httpClientConfig;
    private final HttpClient httpClient;

    public HbotClient(HttpClientConfig httpClientConfig) throws HbotClientConfigException {
        if (httpClientConfig == null) {
            throw new HbotClientConfigException("httpClientConfig is null");
        } else {
            this.httpClientConfig = httpClientConfig;
            this.httpClient = this.createHttpClient(this.httpClientConfig);
        }
    }

    public HbotClient(String authorization) throws HbotClientConfigException {
        this.httpClientConfig = new HttpClientConfig();
        if (authorization != null) {
            this.httpClientConfig.setAuthorization(authorization);
        }

        this.httpClient = this.createHttpClient(this.httpClientConfig);
    }

    protected HttpClient createHttpClient(HttpClientConfig config) throws HbotClientConfigException {
        return new HttpClient(config);
    }

    public Object chat(ChatRequest chatRequest) throws HbotClientConfigException, HbotHttpResponseException {
        Map<String, Object> data = new HashMap();
        data.put("appId", chatRequest.getAppId());
        data.put("query", chatRequest.getQuery());
        data.put("userId", chatRequest.getUserId());
        data.put("searchEngine", chatRequest.getSearchEngine());
        if (chatRequest.getConversationId() != null) {
            data.put("conversationId", chatRequest.getConversationId());
        }

        if (chatRequest.getRequestId() != null) {
            data.put("requestId", chatRequest.getRequestId());
        }

        if (chatRequest.getClientProperties() != null) {
            data.put("clientProperties", chatRequest.getClientProperties());
        }

        if (chatRequest.getFiles() != null) {
            data.put("files", chatRequest.getFiles());
        }

        data.put("stream", chatRequest.getStream());
        if (chatRequest.getStream()) {
            Iterable<HttpResponseEvent> responseIter = this.httpClient.postStream("/api/chat", data, null, 110);
            return this.stream(responseIter, chatRequest.getMessageParser());
        } else {
            String responseText = this.httpClient.post("/api/chat", data, null);
            return new HbotResponse(responseText, new TypeReference<SyncChatResponse>() {
            });
        }
    }

    public Object completion(CompletionRequest completionRequest) throws HbotClientConfigException, HbotHttpResponseException {
        Map<String, Object> data = new HashMap();
        data.put("appId", completionRequest.getAppId());
        data.put("userId", completionRequest.getUserId());
        if (completionRequest.getRequestId() != null) {
            data.put("requestId", completionRequest.getRequestId());
        }

        if (completionRequest.getInputs() != null) {
            data.put("inputs", completionRequest.getInputs());
        }

        if (completionRequest.getClientProperties() != null) {
            data.put("clientProperties", completionRequest.getClientProperties());
        }

        if (completionRequest.getFiles() != null) {
            data.put("files", completionRequest.getFiles());
        }

        data.put("stream", completionRequest.getStream());
        if (completionRequest.getStream()) {
            Iterable<HttpResponseEvent> responseIter = this.httpClient.postStream("/api/completion", data, null, 110);
            return this.stream(responseIter, completionRequest.getMessageParser());
        } else {
            String responseText = this.httpClient.post("/api/completion", data, null);
            return new HbotResponse(responseText, new TypeReference<SyncCompletionResponse>() {
            });
        }
    }

    public HbotResponse<ConversationListResponse> getConversations(ConversationListRequest conversationListRequest) throws HbotClientConfigException, HbotHttpResponseException {
        if (conversationListRequest == null) {
            throw new HbotClientConfigException("conversationListRequest is null");
        } else if (conversationListRequest.getAppId() != null && !conversationListRequest.getAppId().trim().isEmpty()) {
            Map<String, Object> params = new HashMap();
            params.put("appId", conversationListRequest.getAppId());
            if (conversationListRequest.getUserId() != null) {
                params.put("userId", conversationListRequest.getUserId());
            }

            if (conversationListRequest.getSource() != null) {
                params.put("source", conversationListRequest.getSource());
            }

            if (conversationListRequest.getPageNum() != null) {
                params.put("pageNum", conversationListRequest.getPageNum());
            }

            if (conversationListRequest.getPageSize() != null) {
                params.put("pageSize", conversationListRequest.getPageSize());
            }

            if (conversationListRequest.getSortOrder() != null) {
                params.put("sortOrder", conversationListRequest.getSortOrder());
            }

            Map<String, String> stringParams = new HashMap();
            Iterator var4 = params.entrySet().iterator();

            while(var4.hasNext()) {
                Map.Entry<String, Object> entry = (Map.Entry)var4.next();
                if (entry.getValue() != null) {
                    stringParams.put((String)entry.getKey(), entry.getValue().toString());
                }
            }

            try {
                String responseText = this.httpClient.get("/api/conversation/conversations", stringParams, (Map)null);
                return new HbotResponse(responseText, new TypeReference<ConversationListResponse>() {
                });
            } catch (Exception var6) {
                throw new HbotHttpResponseException("Failed to parse response", var6);
            }
        } else {
            throw new HbotClientConfigException("appId is required");
        }
    }

    public HbotResponse<MessageListResponse> getMessages(MessageListRequest messageListRequest) throws HbotClientConfigException, HbotHttpResponseException {
        if (messageListRequest == null) {
            throw new HbotClientConfigException("messageListRequest is null");
        } else if (messageListRequest.getConversationId() != null && !messageListRequest.getConversationId().trim().isEmpty()) {
            Map<String, Object> params = new HashMap();
            params.put("conversationId", messageListRequest.getConversationId());
            if (messageListRequest.getPageNum() != null) {
                params.put("pageNum", messageListRequest.getPageNum());
            }

            if (messageListRequest.getPageSize() != null) {
                params.put("pageSize", messageListRequest.getPageSize());
            }

            if (messageListRequest.getSortOrder() != null) {
                params.put("sortOrder", messageListRequest.getSortOrder());
            }

            Map<String, String> stringParams = new HashMap();
            Iterator var4 = params.entrySet().iterator();

            while(var4.hasNext()) {
                Map.Entry<String, Object> entry = (Map.Entry)var4.next();
                if (entry.getValue() != null) {
                    stringParams.put((String)entry.getKey(), entry.getValue().toString());
                }
            }

            try {
                String responseText = this.httpClient.get("/api/conversation/messages", stringParams, (Map)null);
                return new HbotResponse(responseText, new TypeReference<MessageListResponse>() {
                });
            } catch (Exception var6) {
                throw new HbotHttpResponseException("Failed to parse response", var6);
            }
        } else {
            throw new HbotClientConfigException("conversationId is required");
        }
    }

    public HbotResponse<String> createConversation(String appId) throws HbotClientConfigException, HbotHttpResponseException {
        if (appId != null && !appId.trim().isEmpty()) {
            Map<String, Object> data = new HashMap();
            data.put("appId", appId);

            try {
                String responseText = this.httpClient.post("/api/conversation/create", data, (Map)null);
                return new HbotResponse(responseText, String.class);
            } catch (Exception var4) {
                throw new HbotHttpResponseException("Failed to parse response", var4);
            }
        } else {
            throw new HbotClientConfigException("appId is required");
        }
    }

    public HbotResponse<String> uploadFile(String filePath) throws HbotClientConfigException, HbotHttpResponseException {
        if (filePath != null && !filePath.trim().isEmpty()) {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new HbotClientConfigException("file does not exist: " + filePath);
            } else {
                try {
                    String responseText = this.httpClient.uploadFile("/api/file/upload", file, (Map)null);
                    return new HbotResponse(responseText, String.class);
                } catch (Exception var4) {
                    throw new HbotHttpResponseException("Failed to upload file", var4);
                }
            }
        } else {
            throw new HbotClientConfigException("filePath is required");
        }
    }
    public HbotResponse<Void> deleteDocument(String documentId) throws HbotClientConfigException, HbotHttpResponseException {
        if (documentId != null && !documentId.trim().isEmpty()) {
            Map<String, Object> data = new HashMap();
            data.put("documentId", documentId);

            try {
                String responseText = this.httpClient.delete("/api/datasets/deleteDocument", data, (Map)null);
                return new HbotResponse(responseText, Void.class);
            } catch (Exception var4) {
                throw new HbotHttpResponseException("Failed to delete document", var4);
            }
        } else {
            throw new HbotClientConfigException("documentId is required");
        }
    }

    public HbotResponse<Void> deleteDataset(String datasetId) throws HbotClientConfigException, HbotHttpResponseException {
        if (datasetId != null && !datasetId.trim().isEmpty()) {
            Map<String, Object> data = new HashMap();
            data.put("datasetId", datasetId);

            try {
                String responseText = this.httpClient.delete("/api/datasets/deleteDataset", data, (Map)null);
                return new HbotResponse(responseText, Void.class);
            } catch (Exception var4) {
                throw new HbotHttpResponseException("Failed to delete dataset", var4);
            }
        } else {
            throw new HbotClientConfigException("deleteDatasetRequest is null");
        }
    }
    private Iterable<Map<String, Object>> stream(Iterable<HttpResponseEvent> responseIter, MessageParser messageParser) {
        MessageParser parser = messageParser != null ? messageParser : new MessageParser();
        return () -> {
            return new Iterator<Map<String, Object>>() {
                private final Iterator<HttpResponseEvent> eventIterator = responseIter.iterator();
                private Map<String, Object> nextData = null;
                private boolean hasNextCalled = false;

                public boolean hasNext() {
                    if (this.hasNextCalled) {
                        return this.nextData != null;
                    } else {
                        HttpResponseEvent event;
                        do {
                            if (!this.eventIterator.hasNext()) {
                                this.hasNextCalled = true;
                                this.nextData = null;
                                return false;
                            }

                            event = (HttpResponseEvent)this.eventIterator.next();
                        } while(!parser.needParse(event));

                        try {
                            Map<String, Object> data = parser.parse(event);
                            String type = (String)data.get("type");
                            if ("meta".equals(type)) {
                                this.nextData = data;
                                this.hasNextCalled = true;
                                return true;
                            } else {
                                this.nextData = data;
                                this.hasNextCalled = true;
                                return true;
                            }
                        } catch (HbotServerException var4) {
                            throw new RuntimeException(var4.getMessage(), var4);
                        }
                    }
                }

                public Map<String, Object> next() {
                    if (!this.hasNextCalled) {
                        this.hasNext();
                    }

                    if (this.nextData == null) {
                        throw new NoSuchElementException();
                    } else {
                        Map<String, Object> result = this.nextData;
                        this.nextData = null;
                        this.hasNextCalled = false;
                        return result;
                    }
                }
            };
        };
    }
}
