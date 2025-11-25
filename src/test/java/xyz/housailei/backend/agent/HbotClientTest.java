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
import xyz.housailei.backend.agent.model.request.CompletionRequest;
import xyz.housailei.backend.agent.model.response.HbotResponse;

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
}