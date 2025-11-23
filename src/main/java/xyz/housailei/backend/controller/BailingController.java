package xyz.housailei.backend.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import xyz.housailei.backend.agent.HbotClient;
import xyz.housailei.backend.agent.core.exception.HbotClientConfigException;
import xyz.housailei.backend.agent.core.exception.HbotHttpResponseException;
import xyz.housailei.backend.agent.model.file.File;
import xyz.housailei.backend.agent.model.file.FileType;
import xyz.housailei.backend.agent.model.request.ChatRequest;
import xyz.housailei.backend.agent.model.response.HbotResponse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 白凌相关接口
 *
 * @author liul
 */
@Tag(name = "白凌接口", description = "白凌相关接口")
@RestController
@RequestMapping("/bailing")
@RequiredArgsConstructor
public class BailingController {

    @Resource
    private final HbotClient hbotClient;

    @Operation(summary = "文件流式聊天", description = "支持单个文件上传的流式聊天接口")
    @PostMapping(value = "/stream-chat-with-files", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChatWithFiles(
            @Parameter(description = "应用ID") @RequestParam String appId,
            @Parameter(description = "用户查询") @RequestParam String query,
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "会话ID") @RequestParam(required = false) String conversationId,
            @Parameter(description = "上传的文件") @RequestParam(value = "file", required = false) MultipartFile uploadedFile) {

        // 处理文件上传，返回文件列表的 Mono
        Mono<List<File>> filesMono = Mono.fromCallable(() -> {
            List<File> files = new ArrayList<>();
            if (uploadedFile != null && !uploadedFile.isEmpty()) {
                // 验证文件类型，只允许图片
                if (!isImageFile(uploadedFile.getOriginalFilename())) {
                    throw new RuntimeException("只支持图片文件上传，支持的格式：jpg、jpeg、png、gif、bmp、webp");
                }

                // 将 MultipartFile 保存为临时文件
                Path tempFile = Files.createTempFile("upload_", "_" + uploadedFile.getOriginalFilename());
                uploadedFile.transferTo(tempFile.toFile());

                try {
                    // 使用 HbotClient 的 uploadFile 方法上传文件
                    HbotResponse<String> uploadResponse = hbotClient.uploadFile(tempFile.toString());

                    if (uploadResponse.isSuccess() && uploadResponse.getData() != null) {
                        String fileId = uploadResponse.getData();
                        // 文件类型固定为 IMAGE
                        files.add(new File(fileId, FileType.IMAGE));
                    } else {
                        throw new RuntimeException("文件上传失败: " + uploadResponse.getErrorMsg());
                    }
                } finally {
                    // 删除临时文件
                    Files.deleteIfExists(tempFile);
                }
            }
            return files;
        }).onErrorMap(e -> {
            if (e instanceof RuntimeException) {
                return e;
            }
            return new RuntimeException("文件处理失败: " + e.getMessage(), e);
        });

        // 构建 ChatRequest 并调用流式聊天服务
        return filesMono.flatMapMany(files -> {
            try {
                // 构建 ChatRequest
                ChatRequest chatRequest = new ChatRequest(appId, query, userId, conversationId);
                chatRequest.setFiles(files.isEmpty() ? null : files);
                chatRequest.setStream(true);

                // 调用流式聊天服务
                @SuppressWarnings("unchecked")
                Iterable<Map<String, Object>> streamResponse = (Iterable<Map<String, Object>>) hbotClient.chat(chatRequest);

                // 将流式响应转换为 Flux<ServerSentEvent>
                return Flux.fromIterable(streamResponse)
                        .map(event -> {
                            String eventType = (String) event.getOrDefault("type", "message");
                            String eventData = JSON.toJSONString(event);
                            return ServerSentEvent.<String>builder()
                                    .event(eventType)
                                    .data(eventData)
                                    .build();
                        })
                        .onErrorResume(e -> {
                            String errorMsg = e instanceof HbotClientConfigException || e instanceof HbotHttpResponseException
                                    ? e.getMessage()
                                    : "流式聊天失败：" + e.getMessage();
                            return Flux.just(ServerSentEvent.<String>builder()
                                    .event("error")
                                    .data(JSON.toJSONString(Map.of("error", errorMsg)))
                                    .build());
                        });
            } catch (HbotClientConfigException | HbotHttpResponseException e) {
                return Flux.just(ServerSentEvent.<String>builder()
                        .event("error")
                        .data(JSON.toJSONString(Map.of("error", e.getMessage())))
                        .build());
            } catch (Exception e) {
                return Flux.just(ServerSentEvent.<String>builder()
                        .event("error")
                        .data(JSON.toJSONString(Map.of("error", "流式聊天失败：" + e.getMessage())))
                        .build());
            }
        }).onErrorResume(e -> {
            return Flux.just(ServerSentEvent.<String>builder()
                    .event("error")
                    .data(JSON.toJSONString(Map.of("error", "请求处理失败：" + e.getMessage())))
                    .build());
        });
    }

    /**
     * 判断文件是否为图片类型
     */
    private boolean isImageFile(String filename) {
        if (filename == null) {
            return false;
        }

        String lowerFilename = filename.toLowerCase();
        return lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg") ||
                lowerFilename.endsWith(".png") || lowerFilename.endsWith(".svg") ||
                lowerFilename.endsWith(".bmp") || lowerFilename.endsWith(".webp");
    }
}


