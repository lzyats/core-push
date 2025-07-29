package com.platform.modules.push.service;

import java.util.Collection;

/**
 * <p>
 * 推送 服务层
 * </p>
 */
public interface PushProvider {

    /**
     * 推送（mq）
     */
    void convertAndSend(String content);

    /**
     * 推送（个推）
     */
    void convertAndSend(Collection receiveList, String message);

}
