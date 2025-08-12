package com.platform.modules.push.dto;

import cn.hutool.core.util.NumberUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 消息发送人
 */
@Data
@Accessors(chain = true) // 链式调用
public class PushComments {

    private Long commentId;

    /** 是否发布者 */
    private boolean  source;

    /** 评论人 */
    private String fromUser;

    /** 被评论人 */
    private String toUser;

    /** 评论内容 */
    private String content;

    public PushComments setCommentId(Long commentId) {
        this.commentId = commentId;
        return this;
    }

}
