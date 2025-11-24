package xyz.housailei.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import xyz.housailei.backend.agent.HbotClient;
import xyz.housailei.backend.agent.core.exception.HbotClientConfigException;
import xyz.housailei.backend.agent.core.exception.HbotHttpResponseException;
import xyz.housailei.backend.agent.model.file.File;
import xyz.housailei.backend.agent.model.file.FileType;
import xyz.housailei.backend.agent.model.request.ChatRequest;
import xyz.housailei.backend.agent.model.response.HbotResponse;

import java.io.IOException;
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
    @PostMapping(value = {"/stream-chat-with-file"}, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, Object>> streamChatWithFiles(
            @Parameter(description = "应用ID") @RequestParam String appId,
            @Parameter(description = "用户查询") @RequestParam String query,
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "会话ID") @RequestParam(required = false) String conversationId,
            @Parameter(description = "上传的文件") @RequestParam(value = "file", required = false) MultipartFile uploadedFile) {

        List<File> files;
        try {
            files = buildFileList(uploadedFile);
        } catch (Exception e) {
            RuntimeException runtimeException = e instanceof RuntimeException ? (RuntimeException) e
                    : new RuntimeException("文件处理失败: " + e.getMessage(), e);
            return Flux.error(runtimeException);
        }

        return Flux.defer(() -> {
            try {
                ChatRequest chatRequest = new ChatRequest(appId, query, userId, conversationId);
                chatRequest.setFiles(files.isEmpty() ? null : files);
                chatRequest.setStream(true);

                @SuppressWarnings("unchecked")
                Iterable<Map<String, Object>> streamResponse =
                        (Iterable<Map<String, Object>>) hbotClient.chat(chatRequest);

                return Flux.fromIterable(streamResponse);
            } catch (HbotClientConfigException | HbotHttpResponseException e) {
                return Flux.error(e);
            } catch (Exception e) {
                return Flux.error(new RuntimeException("流式聊天失败：" + e.getMessage(), e));
            }
        });
    }

    private List<File> buildFileList(MultipartFile uploadedFile) throws IOException, HbotHttpResponseException, HbotClientConfigException {
        List<File> files = new ArrayList<>();
        if (uploadedFile == null || uploadedFile.isEmpty()) {
            return files;
        }

        String tempFilePath = saveTempFile(uploadedFile);

        try {
            HbotResponse<String> uploadResponse = hbotClient.uploadFile(tempFilePath);

            if (uploadResponse.isSuccess() && uploadResponse.getData() != null) {
                String contentType = uploadedFile.getContentType();
                FileType fileType = determineFileType(contentType);
                files.add(new File(uploadResponse.getData(), fileType));
            } else {
                throw new RuntimeException("文件上传失败: " + uploadResponse.getErrorMsg());
            }
        } finally {
            java.io.File tempFile = new java.io.File(tempFilePath);
            if (tempFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                tempFile.delete();
            }
        }

        return files;
    }

    private String saveTempFile(MultipartFile uploadedFile) throws IOException {
        String originalFilename = uploadedFile.getOriginalFilename();
        String suffix = originalFilename == null ? ".tmp" : "_" + originalFilename;
        Path tempFile = Files.createTempFile("upload_", suffix);
        uploadedFile.transferTo(tempFile.toFile());
        return tempFile.toString();
    }

    private FileType determineFileType(String contentType) {
        if (contentType == null) {
            return FileType.FILE;
        }

        if (contentType.startsWith("image/")) {
            return FileType.IMAGE;
        }
        if (contentType.startsWith("audio/")) {
            return FileType.AUDIO;
        }
        if (contentType.startsWith("video/")) {
            return FileType.VIDEO;
        }
        return FileType.FILE;
    }
}


