package com.platform.modules.push.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

/**
 * 消息同步
 */
@Data
@Accessors(chain = true) // 链式调用
public class PushMoSync {

    /**
     * 朋友圈ID
     */
    private Long momentId;

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


    public PushMoSync setRemark(String remark) {
        if (!StringUtils.isEmpty(remark)) {
            this.nickname = remark;
        }
        return this;
    }

}
