package com.platform.modules.push.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

/**
 * 消息同步
 */
@Data
@Accessors(chain = true) // 链式调用
public class PushSync {

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

    public PushSync setRemark(String remark) {
        if (!StringUtils.isEmpty(remark)) {
            this.nickname = remark;
        }
        return this;
    }

}
