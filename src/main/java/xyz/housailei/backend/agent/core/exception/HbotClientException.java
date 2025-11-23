package xyz.housailei.backend.agent.core.exception;

public class HbotClientException extends Exception {
    public HbotClientException() {
    }

    public HbotClientException(String message) {
        super(message);
    }

    public HbotClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public HbotClientException(Throwable cause) {
        super(cause);
    }
}
