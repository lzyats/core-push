package com.platform.modules.push.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 消息群组
 */
@Data
@Accessors(chain = true) // 链式调用
public class PushGroup {

    /**
     * 群组id
     */
    private Long groupId;

    /**
     * 群组头像
     */
    private String portrait;

    /**
     * 群组名称
     */
    private String groupName;

    /**
     * 类型
     */
    private String chatTalk;

}
