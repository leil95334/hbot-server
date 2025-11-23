//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package xyz.housailei.backend.agent.core.http;

import lombok.Data;

@Data
public class HttpResponseEvent {
    private String id;
    private String event;
    private String data;

    public HttpResponseEvent(String event, String data) {
        this.event = event;
        this.data = data;
    }

    public HttpResponseEvent(String event, String data, String id) {
        this.event = event;
        this.data = data;
        this.id = id;
    }
}
