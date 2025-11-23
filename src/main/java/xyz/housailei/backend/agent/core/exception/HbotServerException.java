package xyz.housailei.backend.agent.core.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class HbotServerException extends HbotClientException {
    private Map<String, Object> errorContext;

    public HbotServerException(String message) {
        super(message);
    }

    public HbotServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public void setErrorContext(Map<String, Object> errorContext) {
        this.errorContext = errorContext;
    }
}
