package com.platform.modules.push.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 推送通知类型
 */
@Getter
public enum PushBadgerEnum {

    /**
     * 好友
     */
    FRIEND("friend", "chat:badger:friend:{}"),
    /**
     * 群组
     */
    GROUP("group", "chat:badger:group:{}"),
    ;

    @JsonValue
    private String code;
    private String type;

    PushBadgerEnum(String code, String type) {
        this.code = code;
        this.type = type;
    }

}
