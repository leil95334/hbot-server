package xyz.housailei.backend.agent.core.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.housailei.backend.agent.core.exception.HbotClientConfigException;
import xyz.housailei.backend.agent.core.exception.HbotHttpResponseException;

public class HttpClient {
    private static final Logger logger = LoggerFactory.getLogger("Hbot.client");
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType MULTIPART_MEDIA_TYPE = MediaType.parse("multipart/form-data");
    private static final OkHttpClient okHttpClient = (new OkHttpClient.Builder()).connectTimeout(Duration.ofSeconds(30L)).readTimeout(Duration.ofSeconds(120L)).writeTimeout(Duration.ofSeconds(60L)).build();
    private final HttpClientConfig httpClientConfig;

    public HttpClient(HttpClientConfig httpClientConfig) throws HbotClientConfigException {
        if (httpClientConfig == null) {
            throw new HbotClientConfigException("httpClientConfig is null");
        } else {
            this.httpClientConfig = httpClientConfig;
        }
    }

    public String post(String path, Map<String, Object> data, Map<String, String> headers) throws HbotClientConfigException, HbotHttpResponseException {
        String url = this.buildUrl(path);

        try {
            String json = JSON.toJSONString(data);
            RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);
            Request request = (new Request.Builder()).url(url).headers(Headers.of(this.generateHeaders(headers))).post(body).build();
            Response response = okHttpClient.newCall(request).execute();

            String var10;
            try {
                String responseText;
                if (!response.isSuccessful()) {
                    responseText = response.body() != null ? response.body().string() : "Unknown error";
                    logger.error("HTTP request failed, status_code: {}, text: {}", response.code(), responseText);
                    throw new HbotHttpResponseException(response.code(), String.format("HTTP request failed, status_code: %d, text: %s", response.code(), responseText));
                }

                responseText = response.body() != null ? response.body().string() : "";
                logger.info("HTTP request success, status_code: {}, text: {}", response.code(), responseText);
                var10 = responseText;
            } catch (Throwable var12) {
                if (response != null) {
                    try {
                        response.close();
                    } catch (Throwable var11) {
                        var12.addSuppressed(var11);
                    }
                }

                throw var12;
            }

            if (response != null) {
                response.close();
            }

            return var10;
        } catch (JSONException var13) {
            throw new HbotClientConfigException("Failed to serialize request data", var13);
        } catch (IOException var14) {
            throw new HbotHttpResponseException("Failed to execute request", var14);
        }
    }

    public Iterable<HttpResponseEvent> postStream(String path, Map<String, Object> data, Map<String, String> headers, int timeout) throws HbotClientConfigException {
        String url = this.buildUrl(path);

        try {
            String json = JSON.toJSONString(data);
            RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);
            Request request = (new Request.Builder()).url(url).headers(Headers.of(this.generateHeaders(headers))).post(body).build();
            final BlockingQueue<HttpResponseEvent> eventQueue = new LinkedBlockingQueue();
            EventSourceListener listener = new EventSourceListener() {
                public void onEvent(EventSource eventSource, String id, String type, String data) {
                    eventQueue.add(new HttpResponseEvent(type, data, id));
                }

                public void onFailure(EventSource eventSource, Throwable t, Response response) {
                    String errorMessage = "Request Hbot failed with HTTP code";
                    if (response != null) {
                        try {
                            errorMessage = errorMessage + ": " + response.code() + ", error message: " + ((ResponseBody) Objects.requireNonNull(response.body())).string();
                        } catch (IOException var8) {
                            HttpClient.logger.error("Failed to get response body", var8);
                        }
                    }

                    HttpClient.logger.error(errorMessage, t);
                    Map<String, Object> errorContext = new HashMap();
                    if (t != null) {
                        errorContext.put("errorMsg", t.getMessage());
                    } else {
                        errorContext.put("errorMsg", errorMessage);
                    }

                    try {
                        eventQueue.add(new HttpResponseEvent("error", JSON.toJSONString(errorContext)));
                    } catch (JSONException var7) {
                        eventQueue.add(new HttpResponseEvent("error", "{\"errorMsg\":\"Failed to serialize error context\"}"));
                    }

                    eventSource.cancel();
                }

                public void onClosed(EventSource eventSource) {
                    eventQueue.add(new HttpResponseEvent("close", "Connection closed"));
                }
            };
            OkHttpClient customClient = okHttpClient.newBuilder().readTimeout((long) timeout, TimeUnit.SECONDS).build();
            EventSource.Factory factory = EventSources.createFactory(customClient);
            EventSource eventSource = factory.newEventSource(request, listener);
            return () -> {
                return new Iterator<HttpResponseEvent>() {
                    private HttpResponseEvent nextEvent;
                    private boolean closed = false;

                    public boolean hasNext() {
                        if (this.closed) {
                            return false;
                        } else if (this.nextEvent != null) {
                            return true;
                        } else {
                            try {
                                this.nextEvent = (HttpResponseEvent) eventQueue.poll((long) timeout, TimeUnit.SECONDS);
                                if (this.nextEvent != null && !"close".equals(this.nextEvent.getEvent())) {
                                    return true;
                                } else {
                                    this.closed = true;
                                    eventSource.cancel();
                                    return false;
                                }
                            } catch (InterruptedException var2) {
                                Thread.currentThread().interrupt();
                                this.closed = true;
                                eventSource.cancel();
                                return false;
                            }
                        }
                    }

                    public HttpResponseEvent next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        } else {
                            HttpResponseEvent event = this.nextEvent;
                            this.nextEvent = null;
                            return event;
                        }
                    }
                };
            };
        } catch (JSONException var14) {
            throw new HbotClientConfigException("Failed to serialize request data", var14);
        }
    }

    public String get(String path, Map<String, String> query, Map<String, String> headers) throws HbotHttpResponseException {
        String url = this.buildUrl(path);
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        if (query != null) {
            Iterator var6 = query.entrySet().iterator();

            while (var6.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry) var6.next();
                httpBuilder.addQueryParameter((String) entry.getKey(), (String) entry.getValue());
            }
        }

        Request request = (new Request.Builder()).url(httpBuilder.build()).headers(Headers.of(this.generateHeaders(headers))).get().build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            String responseText = response.body() != null ? response.body().string() : "";
            logger.info("HTTP request success, status_code: {}, text: {}", response.code(), responseText);
            return responseText;
        } catch (IOException var9) {
            throw new HbotHttpResponseException("Failed to execute request", var9);
        }
    }

    public String uploadFile(String path, File file, Map<String, String> headers) throws HbotClientConfigException, HbotHttpResponseException {
        String url = this.buildUrl(path);

        try {
            RequestBody fileBody = RequestBody.create(file, MediaType.parse("application/octet-stream"));
            MultipartBody multipartBody = (new MultipartBody.Builder()).setType(MultipartBody.FORM).addFormDataPart("file", file.getName(), fileBody).build();
            Map<String, String> uploadHeaders = this.generateHeaders(headers);
            uploadHeaders.remove("Content-Type");
            Request request = (new Request.Builder()).url(url).headers(Headers.of(uploadHeaders)).post(multipartBody).build();
            Response response = okHttpClient.newCall(request).execute();

            String var11;
            try {
                String responseText;
                if (!response.isSuccessful()) {
                    responseText = response.body() != null ? response.body().string() : "Unknown error";
                    logger.error("File upload failed, status_code: {}, text: {}", response.code(), responseText);
                    throw new HbotHttpResponseException(response.code(), String.format("File upload failed, status_code: %d, text: %s", response.code(), responseText));
                }

                responseText = response.body() != null ? response.body().string() : "";
                logger.info("File upload success, status_code: {}, text: {}", response.code(), responseText);
                var11 = responseText;
            } catch (Throwable var13) {
                if (response != null) {
                    try {
                        response.close();
                    } catch (Throwable var12) {
                        var13.addSuppressed(var12);
                    }
                }

                throw var13;
            }

            if (response != null) {
                response.close();
            }

            return var11;
        } catch (IOException var14) {
            throw new HbotHttpResponseException("Failed to upload file", var14);
        }
    }

    public String delete(String path, Map<String, Object> data, Map<String, String> headers) throws HbotClientConfigException, HbotHttpResponseException {
        String url = this.buildUrl(path);

        try {
            String json = JSON.toJSONString(data);
            RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);
            Request request = (new Request.Builder()).url(url).headers(Headers.of(this.generateHeaders(headers))).delete(body).build();
            Response response = okHttpClient.newCall(request).execute();

            String var10;
            try {
                String responseText;
                if (!response.isSuccessful()) {
                    responseText = response.body() != null ? response.body().string() : "Unknown error";
                    logger.error("HTTP request failed, status_code: {}, text: {}", response.code(), responseText);
                    throw new HbotHttpResponseException(response.code(), String.format("HTTP request failed, status_code: %d, text: %s", response.code(), responseText));
                }

                responseText = response.body() != null ? response.body().string() : "";
                logger.info("HTTP request success, status_code: {}, text: {}", response.code(), responseText);
                var10 = responseText;
            } catch (Throwable var12) {
                if (response != null) {
                    try {
                        response.close();
                    } catch (Throwable var11) {
                        var12.addSuppressed(var11);
                    }
                }

                throw var12;
            }

            if (response != null) {
                response.close();
            }

            return var10;
        } catch (JSONException var13) {
            throw new HbotClientConfigException("Failed to serialize request data", var13);
        } catch (IOException var14) {
            throw new HbotHttpResponseException("Failed to execute request", var14);
        }
    }

    private String buildUrl(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        return String.format("%s://%s%s", this.httpClientConfig.getSchema(), this.httpClientConfig.getHost(), path);
    }

    private Map<String, String> generateHeaders(Map<String, String> headers) {
        Map<String, String> result = new HashMap();
        if (headers != null) {
            result.putAll(headers);
        }

        if (this.httpClientConfig.getAuthorization() != null) {
            result.put("Authorization", this.httpClientConfig.getAuthorization());
        }

        if (result.get("Content-Type") == null) {
            result.put("Content-Type", "application/json");
        }

        result.put("source", "AGENT_SDK");
        return result;
    }
}
