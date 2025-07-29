package com.platform.modules.push.message;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 推送消息
 */
@Data
@Accessors(chain = true) // 链式调用
public class PushMessageBase {

    /**
     * 推送类型
     */
    protected String pushType = "msg";

    /**
     * 推送数据
     */
    protected Object pushData;

}
