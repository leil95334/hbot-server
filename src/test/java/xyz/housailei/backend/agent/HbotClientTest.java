package xyz.housailei.backend.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import xyz.housailei.backend.agent.core.exception.HbotClientConfigException;
import xyz.housailei.backend.agent.core.exception.HbotHttpResponseException;
import xyz.housailei.backend.agent.model.file.File;
import xyz.housailei.backend.agent.model.file.FileType;
import xyz.housailei.backend.agent.model.request.ChatRequest;
import xyz.housailei.backend.agent.model.request.CompletionRequest;
import xyz.housailei.backend.agent.model.request.ConversationListRequest;
import xyz.housailei.backend.agent.model.request.MessageListRequest;
import xyz.housailei.backend.agent.model.response.ConversationListResponse;
import xyz.housailei.backend.agent.model.response.HbotResponse;
import xyz.housailei.backend.agent.model.response.MessageListResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Date 2025/11/25 14:18
 * @Author hsl
 */
@SpringBootTest
class HbotClientTest {

    @Resource
    private HbotClient hbotClient;

    @Test
    void completion() throws HbotHttpResponseException, HbotClientConfigException, IOException {

        String query = "提取图片上的文字";

        MultipartFile uploadedFile = new MockMultipartFile("file", "test.png", "image/png", "This is a test image file.".getBytes());
        List<File> files;
        files = buildFileList(uploadedFile);

        CompletionRequest completionRequest = new CompletionRequest();
        completionRequest.setAppId("202511AP0vDU00565908");
        completionRequest.setUserId("1001");
        completionRequest.setInputs(Map.of("feqeyrj5", query, "z3qftuk6", "https://mdn.alipayobjects.com/cto_doraemon/afts/img/EUXMQ7srwVMAAAAAR2AAAAgAehe3AABr/original?t=AP1gs1ZSV-adaEu5J_PV5cd_uwoKNpLNIxNc8DCTIN0DAAAAZAAAtxdpJXHb"));
        completionRequest.setStream(true);
        completionRequest.setFiles(files);
        try {
            Iterable<Map<String, Object>> response = (Iterable<Map<String, Object>>) hbotClient.completion(completionRequest);
            for (Map<String, Object> chunk : response) {
                System.out.println(chunk);
            }
        } catch (HbotClientConfigException | HbotHttpResponseException e) {
            throw new RuntimeException(e);
        }
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
                // noinspection ResultOfMethodCallIgnored
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

    @Test
    void getConversations() {

        ConversationListRequest conversationListRequest = new ConversationListRequest();
        conversationListRequest.setAppId("202511APSdf200565893");
        conversationListRequest.setUserId("20251128FkT358658129");
        conversationListRequest.setPageNum(1);
        conversationListRequest.setPageSize(10);
        conversationListRequest.setSortOrder("DESC");
        try {
            HbotResponse<ConversationListResponse> response = hbotClient.getConversations(conversationListRequest);
            System.out.println(response);
        } catch (HbotClientConfigException | HbotHttpResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getMessages() {
        MessageListRequest messageListRequest = new MessageListRequest();
        messageListRequest.setConversationId("20251128xW9b58662769");
        messageListRequest.setPageNum(1);
        messageListRequest.setPageSize(10);
        messageListRequest.setSortOrder("DESC");
        try {
            HbotResponse<MessageListResponse> response = hbotClient.getMessages(messageListRequest);
            System.out.println(response);
        } catch (HbotClientConfigException | HbotHttpResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createConversation() {
        try {
            HbotResponse<String> response = hbotClient.createConversation("202511APSdf200565893");
            System.out.println(response);
        } catch (HbotClientConfigException | HbotHttpResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void chat() {
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setAppId("202511APY9A100562855");
        chatRequest.setQuery("你好");
        chatRequest.setUserId("999"); // 用户ID 要求必穿 但是传了 不会给你返回
        chatRequest.setStream(true);
        try {
            Iterable<Map<String, Object>>  response = (Iterable<Map<String, Object>>) hbotClient.chat(chatRequest);
            for (Map<String, Object> chunk : response) {
                System.out.println(chunk);
            }
        } catch (HbotClientConfigException | HbotHttpResponseException e) {
            throw new RuntimeException(e);
        }
    }
}