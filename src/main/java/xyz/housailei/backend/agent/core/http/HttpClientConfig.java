//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package xyz.housailei.backend.agent.core.http;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class HttpClientConfig {
    private String authorization;
    private String schema;
    private String host;

    public HttpClientConfig() {
        this.schema = "https";
        this.host = "api.tbox.cn";
        this.authorization = "TBox-23b16e8eb89c477b9daf5c8b181a856e";
    }

    public HttpClientConfig(String authorization) {
        this();
        this.authorization = authorization;
    }

    public HttpClientConfig(String authorization, String schema, String host) {
        this.authorization = authorization;
        this.schema = schema != null ? schema : "https";
        this.host = host != null ? host : "api.tbox.cn";
    }
}
