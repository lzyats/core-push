package com.platform.modules.push.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 消息发送人
 */
@Data
@Accessors(chain = true) // 链式调用
public class PushMedias {

    private Long mediaId;

    /** 媒体类型 */
    private Integer type;

    /** 媒体资源 */
    private String url;

    /** 缩略图 */
    private String thumbnail;

    public PushMedias setMediaId(Long mediaId) {
        this.mediaId = mediaId;
        return this;
    }

}
