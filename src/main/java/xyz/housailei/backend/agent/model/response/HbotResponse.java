package xyz.housailei.backend.agent.model.response;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HbotResponse<T> {
    private String errorCode;
    private String errorMsg;
    private T data;
    private String traceId;


    public HbotResponse(String responseText, TypeReference<T> typeReference) {
        JSONObject jsonResponse = JSON.parseObject(responseText);
        this.errorCode = jsonResponse.getString("errorCode");
        this.errorMsg = jsonResponse.getString("errorMsg");
        this.traceId = jsonResponse.getString("traceId");
        Object dataValue = jsonResponse.get("data");
        if (dataValue == null) {
            this.data = null;
        } else if (dataValue instanceof JSONObject) {
            this.data = JSON.parseObject(((JSONObject) dataValue).toJSONString(), typeReference, new Feature[0]);
        } else {
            this.data = (T) dataValue;
        }

    }

    public HbotResponse(String responseText, Class<T> dataClass) {
        JSONObject jsonResponse = JSON.parseObject(responseText);
        this.errorCode = jsonResponse.getString("errorCode");
        this.errorMsg = jsonResponse.getString("errorMsg");
        this.traceId = jsonResponse.getString("traceId");
        Object dataValue = jsonResponse.get("data");
        if (dataValue == null) {
            this.data = null;
        } else if (dataValue instanceof JSONObject) {
            this.data = JSON.parseObject(((JSONObject) dataValue).toJSONString(), dataClass);
        } else {
            this.data = (T) dataValue;
        }

    }

    public boolean isSuccess() {
        return "0".equals(this.errorCode);
    }

    public String toString() {
        return "HbotResponse{errorCode='" + this.errorCode + '\'' + ", errorMsg='" + this.errorMsg + '\'' + ", data=" + this.data + ", traceId='" + this.traceId + '\'' + '}';
    }
}
