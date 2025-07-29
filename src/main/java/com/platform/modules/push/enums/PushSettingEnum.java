package com.platform.modules.push.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 推送设置类型
 */
@Getter
public enum PushSettingEnum {
    /**
     * 系统
     */
    SYS("sys"),
    /**
     * 我的
     */
    MINE("mine"),
    /**
     * 好友
     */
    FRIEND("friend"),
    /**
     * 群组
     */
    GROUP("group"),
    /**
     * 服务号
     */
    ROBOT("robot"),
    /**
     * 小红点
     */
    BADGER("badger"),
    ;

    @JsonValue
    private String code;

    PushSettingEnum(String code) {
        this.code = code;
    }

}
