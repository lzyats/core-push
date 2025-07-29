package com.platform.modules.push.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 推送消息类型
 */
@Getter
public enum PushBoxEnum {

    /**
     * 系统
     */
    SYSTEM("system", "系统通知"),
    /**
     * 交易
     */
    TRADE("trade", "交易通知"),
    ;

    @JsonValue
    private String code;
    private String info;

    PushBoxEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

}
