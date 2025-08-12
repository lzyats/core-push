package com.platform.modules.push.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 推送设置类型
 */
@Getter
public enum PushMomentEnum {
    /**
     * 朋友圈
     */
    MOMENT("moment"),
    /**
     * 点赞
     */
    LIKES("likes"),
    /**
     * 评论
     */
    COMMENTS("comments"),
    /**
     * 媒体文件
     */
    MEDIAS("medias"),
    ;

    @JsonValue
    private String code;

    PushMomentEnum(String code) {
        this.code = code;
    }

}
