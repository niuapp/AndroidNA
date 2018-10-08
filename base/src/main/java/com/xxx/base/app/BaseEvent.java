package com.xxx.base.app;

/**
 * Created by niuapp on 2018/9/25 14:56.
 * Project : AndroidNA.
 * Email : 345485985@qq.com
 * -->
 */
public class BaseEvent {

    private String eventType;

    public BaseEvent(String eventType) {
        this.eventType = eventType;
    }

    public String getEventType() {
        return eventType;
    }
}
