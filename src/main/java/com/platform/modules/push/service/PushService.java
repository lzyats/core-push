package com.platform.modules.push.service;

import cn.hutool.json.JSONObject;
import com.platform.modules.push.dto.PushFrom;
import com.platform.modules.push.dto.PushGroup;
import com.platform.modules.push.dto.PushSetting;
import com.platform.modules.push.dto.PushSync;
import com.platform.modules.push.enums.PushAuditEnum;
import com.platform.modules.push.enums.PushBadgerEnum;
import com.platform.modules.push.enums.PushMsgTypeEnum;

import java.util.List;

/**
 * <p>
 * 用户推送 服务层
 * </p>
 */
public interface PushService {

    /**
     * 扫码登录
     */
    void pushScan(String token, String message);

    /**
     * 发送设置-在线全员
     */
    void pushSetting(PushSetting setting);

    /**
     * 发送设置-在线指定
     */
    void pushSetting(PushSetting setting, List<Long> receiveList);

    /**
     * 发送审核
     */
    void pushAudit(PushAuditEnum pushAudit);

    /**
     * 发送消息-个人
     */
    void pushSingle(PushFrom pushFrom, Long receiveId, String content, PushMsgTypeEnum msgType);

    /**
     * 发送消息-个人
     */
    void pushSingle(PushFrom pushFrom, List<Long> receiveList, String content, PushMsgTypeEnum msgType);

    /**
     * 发送消息-同步
     */
    void pushSync(PushFrom pushFrom, PushSync pushSync, String content, PushMsgTypeEnum msgType);

    /**
     * 发送消息-群组
     */
    void pushGroup(PushFrom pushFrom, PushGroup pushGroup, List<Long> receiveList, String content, PushMsgTypeEnum msgType);

    /**
     * 发送消息-badger
     */
    void pushBadger(Long receiveId, PushBadgerEnum badgerEnum, List<Long> principal);

    /**
     * 拉取消息
     */
    List<JSONObject> pullMsg(Long userId, String lastId, int limit);

    /**
     * 删除消息
     */
    void removeMsg(Long userId, List<String> dataList);

    /**
     * 删除消息
     */
    void removeMsg(Long userId);

    /**
     * 清理消息
     */
    void clearMsg(Long userId, Long groupId);

    /**
     * 清理消息
     */
    void clearMsg(Long groupId);

    /**
     * 撤回消息
     */
    void recallMsg(List<String> dataList);

    /**
     * 修改消息
     */
    void editMsg(Long msgId, Long syncId, JSONObject content);

}
