package xyz.housailei.backend.agent.core.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class HbotServerException extends HbotClientException {
    private Map<String, Object> errorContext;

    public HbotServerException(String message) {
        super(message);
    }

    public HbotServerException(String message, Throwable cause) {
        super(message, cause);
    }

}
