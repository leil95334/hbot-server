package xyz.housailei.backend.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.housailei.backend.agent.HbotClient;
import xyz.housailei.backend.agent.core.exception.HbotClientConfigException;
import xyz.housailei.backend.agent.core.http.HttpClientConfig;

/**
 * 白凌 SDK 配置
 * 使用统一的 HTTP 客户端配置，支持 HTTP/2 协议
 */
@Configuration
public class BailingConfig {

    @Resource
    private HttpClientConfig bailingHttpClientConfig;

    /**
     * 创建 TboxClient，使用 HTTP/2 协议
     */
    @Bean
    public HbotClient hbotClient() throws HbotClientConfigException {
        return new HbotClient(bailingHttpClientConfig);
    }
}
