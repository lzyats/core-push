package com.platform.modules.push.service;

import cn.hutool.json.JSONObject;
import com.platform.modules.push.dto.*;
import com.platform.modules.push.enums.PushAuditEnum;
import com.platform.modules.push.enums.PushBadgerEnum;
import com.platform.modules.push.enums.PushMomentEnum;
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
     * 发送设置-在线指定
     */
    void pushMoment(PushFrom pushFrom,PushMoment setting, List<Long> receiveList);


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
     * 发送朋友圈消息-同步
     */
    void pushMomentSync(PushFrom pushFrom, PushMoment pushMoment,List<Long> receiveList,PushMomentEnum msgType);
    /**
     * 存储异步消息不发送
     */

    void pushMomentSync(PushFrom pushFrom, PushMoment pushMoment, List<Long> receiveList,PushMomentEnum msgType,Boolean onlysave);

    /**
     * 发送消息-群组
     */
    void pushGroup(PushFrom pushFrom, PushGroup pushGroup, List<Long> receiveList, String content, PushMsgTypeEnum msgType);

    /**
     * 发送消息-badger
     */
    void pushBadger(Long receiveId, PushBadgerEnum badgerEnum, List<Long> principal);

    /**
     * 发送消息-badger
     */
    void pushBadger(List<Long> receiveList, PushBadgerEnum badgerEnum, boolean principal);

    /**
     * 拉取消息
     */
    List<JSONObject> pullMsg(Long userId, String lastId, int limit);

    /**
     * 拉取朋友圈消息
     */
    List<JSONObject> pullMomentMsg(Long userId, String lastId, int limit);

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

    /**
     * 根据接收者ID获取存储的朋友圈消息列表
     * @param receiveId 接收者ID
     * @param lastId 最后一条消息ID（用于分页，空则从最新开始）
     * @param limit 分页大小
     * @return 消息列表
     */
    List<JSONObject> getStoredMomentMsgByReceiveId(String receiveId, String lastId, int limit);
    /**
     * 批量向多个接收者发送各自存储的朋友圈消息
     * @param receiveIds 接收者ID列表
     * @param limit 每个接收者的消息数量限制
     */
    void pushStoredMomentMsg(List<String> receiveIds, int limit);
    /**
     * 根据接收者ID发送Redis中存储的朋友圈消息
     * @param receiveId 接收者ID（单个接收者）
     * @param lastId 最后一条消息ID（用于分页，空则发送所有）
     * @param limit 发送数量限制（0则发送所有）
     */

    void pushStoredMomentMsg(String receiveId, String lastId, int limit);

}
