package com.platform.modules.push.dto;

import cn.hutool.core.util.NumberUtil;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 消息发送人
 */
@Data
@Accessors(chain = true) // 链式调用
public class PushFrom {

    /**
     * 消息id
     */
    private Long msgId;

    /**
     * 同步id
     */
    private Long syncId;

    /**
     * 群组id
     */
    private String groupId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户头像
     */
    private String portrait;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 类型
     */
    private String chatTalk;

    /**
     * 签名
     */
    private String sign;

    /**
     * 头衔
     */
    private String title;

    public PushFrom setGroupId(Long groupId) {
        this.groupId = NumberUtil.toStr(groupId);
        return this;
    }

    public PushFrom setGroupId(Long robotId, Long userId) {
        this.groupId = robotId + "" + userId;
        return this;
    }

}
