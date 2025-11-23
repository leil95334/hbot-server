package xyz.housailei.backend.agent.model.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.housailei.backend.agent.core.exception.HbotServerException;
import xyz.housailei.backend.agent.core.http.HttpResponseEvent;

public class MessageParser {
    private static final Logger logger = LoggerFactory.getLogger("Hbot.client");
    private final Map<String, Map<String, Object>> answersHolder = new HashMap();

    public MessageParser() {
    }

    public boolean needParse(HttpResponseEvent responseEvent) {
        return "message".equals(responseEvent.getEvent()) || "error".equals(responseEvent.getEvent());
    }

    public Map<String, Object> parse(HttpResponseEvent responseEvent) throws HbotServerException {
        if ("error".equals(responseEvent.getEvent())) {
            this.parseError(responseEvent);
            return new HashMap();
        } else {
            HashMap errorResult;
            if ("message".equals(responseEvent.getEvent())) {
                try {
                    JSONObject data = JSON.parseObject(responseEvent.getData());
                    switch (data.getString("type")) {
                        case "meta":
                            return this.parseMetaMessage(data);
                        case "header":
                            return this.parseHeaderMessage(data);
                        case "chunk":
                            return this.parseChunkMessage(data);
                        case "revoke":
                            return this.parseRevokeMessage(data);
                        case "error":
                            return this.parseErrorMessage(data);
                        case "charge":
                            return this.parseChargeMessage(data);
                        case "end":
                            return this.parseEndMessage(data);
                        case "unknown":
                            return this.parseUnknownMessage(data);
                        case "followup":
                            return this.parseFollowupMessage(data);
                        default:
                            return this.convertJsonNodeToMap(data);
                    }
                } catch (JSONException var6) {
                    logger.error("Failed to parse response data", var6);
                    errorResult = new HashMap();
                    errorResult.put("error", "Failed to parse response data: " + var6.getMessage());
                    return errorResult;
                }
            } else {
                try {
                    return (Map)JSON.parseObject(responseEvent.getData(), HashMap.class);
                } catch (JSONException var7) {
                    logger.error("Failed to parse response data", var7);
                    errorResult = new HashMap();
                    errorResult.put("error", "Failed to parse response data: " + var7.getMessage());
                    return errorResult;
                }
            }
        }
    }

    private Map<String, Object> parseChunkMessage(JSONObject data) {
        String lane = data.getString("lane");
        if (lane == null) {
            lane = "default";
        }

        Map<String, Object> result = this.convertJsonNodeToMap(data);
        JSONObject payloadNode = data.getJSONObject("payload");
        String payloadText = payloadNode != null ? payloadNode.toJSONString() : "{}";

        try {
            JSONObject payload = JSON.parseObject(payloadText);
            result.put("payload", this.convertJsonNodeToMap(payload));
            if (this.answersHolder.containsKey(lane)) {
                String mediaType = (String)((Map)this.answersHolder.get(lane)).get("type");
                if ("text".equals(mediaType)) {
                    Map<String, Object> payloadMap = (Map)result.get("payload");
                    String text = (String)payloadMap.getOrDefault("text", "");
                    String currentData = (String)((Map)this.answersHolder.get(lane)).get("data");
                    ((Map)this.answersHolder.get(lane)).put("data", currentData + text);
                }

                List<Map<String, Object>> messages = (List)((Map)this.answersHolder.get(lane)).get("messages");
                messages.add(result);
            }
        } catch (JSONException var11) {
            logger.error("Failed to parse payload", var11);
        }

        return result;
    }

    private Map<String, Object> parseMetaMessage(JSONObject data) {
        return this.convertJsonNodeToMap(data);
    }

    private Map<String, Object> parseHeaderMessage(JSONObject data) {
        Map<String, Object> result = this.convertJsonNodeToMap(data);
        String lane = data.getString("lane");
        if (lane == null) {
            lane = "default";
        }

        JSONObject payloadNode = data.getJSONObject("payload");
        String payloadText = payloadNode != null ? payloadNode.toJSONString() : "{}";

        try {
            JSONObject payload = JSON.parseObject(payloadText);
            result.put("payload", this.convertJsonNodeToMap(payload));
            Map<String, Object> payloadMap = this.convertJsonNodeToMap(payload);
            String mediaType = (String)payloadMap.get("mediaType");
            if (!this.answersHolder.containsKey(lane)) {
                Map<String, Object> laneData = new HashMap();
                laneData.put("type", mediaType);
                laneData.put("header", result);
                laneData.put("data", "");
                laneData.put("messages", new ArrayList());
                this.answersHolder.put(lane, laneData);
            } else {
                ((Map)this.answersHolder.get(lane)).put("header", result);
                ((Map)this.answersHolder.get(lane)).put("type", mediaType);
            }
        } catch (JSONException var10) {
            logger.error("Failed to parse payload", var10);
        }

        return result;
    }

    private Map<String, Object> parseRevokeMessage(JSONObject data) {
        return this.convertJsonNodeToMap(data);
    }

    private Map<String, Object> parseErrorMessage(JSONObject data) {
        return this.convertJsonNodeToMap(data);
    }

    private Map<String, Object> parseChargeMessage(JSONObject data) {
        return this.convertJsonNodeToMap(data);
    }

    private Map<String, Object> parseEndMessage(JSONObject data) {
        return this.convertJsonNodeToMap(data);
    }

    private Map<String, Object> parseUnknownMessage(JSONObject data) {
        return this.convertJsonNodeToMap(data);
    }

    private Map<String, Object> parseFollowupMessage(JSONObject data) {
        return this.convertJsonNodeToMap(data);
    }

    private void parseError(HttpResponseEvent responseEvent) throws HbotServerException {
        try {
            JSONObject errorContext = JSON.parseObject(responseEvent.getData());
            String message = "unknown error";
            if (errorContext.containsKey("payload")) {
                JSONObject payload = errorContext.getJSONObject("payload");
                if (payload != null) {
                    message = payload.getString("errorMsg");
                    if (message == null) {
                        message = "unknown error";
                    }
                }
            } else if (errorContext.containsKey("errorMsg")) {
                message = errorContext.getString("errorMsg");
                if (message == null) {
                    message = "unknown error";
                }
            }

            HbotServerException exception = new HbotServerException(message);
            exception.setErrorContext(errorContext);
            throw exception;
        } catch (JSONException var5) {
            throw new HbotServerException("Error parsing server error response", var5);
        }
    }

    public Map<String, Map<String, Object>> getAnswersHolder() {
        return this.answersHolder;
    }

    private Map<String, Object> convertJsonNodeToMap(JSONObject node) {
        try {
            return (Map)JSON.parseObject(node.toJSONString(), HashMap.class);
        } catch (Exception var3) {
            logger.error("Error converting JSONObject to Map", var3);
            return new HashMap();
        }
    }
}
