package xyz.housailei.backend.agent.core.exception;

import lombok.Getter;

@Getter
public class HbotHttpResponseException extends HbotClientException {
    private int statusCode;

    public HbotHttpResponseException(String message) {
        super(message);
    }

    public HbotHttpResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HbotHttpResponseException(String message, Throwable cause) {
        super(message, cause);
    }

}
