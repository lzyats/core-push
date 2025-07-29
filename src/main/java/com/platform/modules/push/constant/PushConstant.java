package com.platform.modules.push.constant;

/**
 * 推送常量
 */
public class PushConstant {

    /**
     * 聊天消息:push:msg:{消息id}
     * 聊天消息:push:msg:123456789
     */
    public static final String PUSH_MSG = "push:msg:{}";
    /**
     * 聊天消息时间（天）
     */
    public static final Integer PUSH_TIME = 7;
    /**
     * 聊天用户:push:user:{date}:{用户id}
     * 聊天用户:push:user:20250519:123456789
     */
    public static final String PUSH_USER = "push:user:{}:{}";
    /**
     * 聊天用户:push:group:{群组id}
     * 聊天用户:push:group:123456789
     */
    public static final String PUSH_GROUP = "push:group:{}";
    /**
     * 推送token:push:token
     */
    public static final String PUSH_TOKEN = "push:token";

}
