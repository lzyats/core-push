package com.platform.modules.push.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.platform.common.constant.HeadConstant;
import com.platform.common.enums.ChannelEnum;
import com.platform.common.redis.RedisUtils;
import com.platform.common.web.vo.LabelVo;
import com.platform.modules.push.constant.PushConstant;
import com.platform.modules.push.dto.PushFrom;
import com.platform.modules.push.dto.PushGroup;
import com.platform.modules.push.dto.PushSetting;
import com.platform.modules.push.dto.PushSync;
import com.platform.modules.push.enums.PushAuditEnum;
import com.platform.modules.push.enums.PushBadgerEnum;
import com.platform.modules.push.enums.PushMsgTypeEnum;
import com.platform.modules.push.enums.PushSettingEnum;
import com.platform.modules.push.message.PushMessageMsg;
import com.platform.modules.push.message.PushMessageSetting;
import com.platform.modules.push.message.PushMessageSync;
import com.platform.modules.push.service.PushProvider;
import com.platform.modules.push.service.PushService;
import com.platform.modules.push.utils.RedisOther;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户推送 服务层
 * </p>
 */
@Service("pushService")
@Slf4j
public class PushServiceImpl implements PushService {

    @Autowired
    private PushProvider pushProvider;

    @Autowired(required = false)
    private RedisUtils redisUtils;

    @Autowired(required = false)
    private RedisOther redisOther;

    @Override
    public void pushScan(String token, String message) {
        // 接收人员
        List<String> receiveList = Arrays.asList(token);
        // 发送消息
        this.sendMsg(receiveList, message, ChannelEnum.SCAN);
    }

    @Override
    public void pushSetting(PushSetting setting) {
        // 消息组装
        PushMessageSetting messageSetting = new PushMessageSetting(setting);
        String message = JSONUtil.toJsonStr(messageSetting);
        // 发送消息
        this.sendMsg(new ArrayList(), message, ChannelEnum.ALL);
    }

    @Override
    public void pushSetting(PushSetting setting, List<Long> receiveList) {
        // 消息组装
        PushMessageSetting messageSetting = new PushMessageSetting(setting);
        String message = JSONUtil.toJsonStr(messageSetting);
        // 发送消息
        this.sendMsg(receiveList, message, ChannelEnum.MSG);
    }

    @Override
    public void pushAudit(PushAuditEnum pushAudit) {
        // 组装对象
        LabelVo labelVo = new LabelVo()
                .setLabel(pushAudit.getCode())
                .setValue(NumberUtil.toStr(0));
        // 异步执行
        ThreadUtil.execAsync(() -> {
            redisUtils.convertAndSend(HeadConstant.REDIS_CHANNEL_ADMIN, JSONUtil.toJsonStr(labelVo));
        });
    }

    @Override
    public void pushSingle(PushFrom pushFrom, Long receiveId, String content, PushMsgTypeEnum msgType) {
        List<Long> receiveList = Arrays.asList(receiveId);
        // 发送消息
        this.pushMsg(pushFrom, null, receiveList, content, msgType);
    }

    @Override
    public void pushSingle(PushFrom pushFrom, List<Long> receiveList, String content, PushMsgTypeEnum msgType) {
        // 发送消息
        this.pushMsg(pushFrom, null, receiveList, content, msgType);
    }

    @Override
    public void pushSync(PushFrom pushFrom, PushSync pushSync, String content, PushMsgTypeEnum msgType) {
        // 消息组装
        PushMessageSync messageSync = new PushMessageSync(pushFrom, pushSync, msgType, content);
        // 格式消息
        String message = JSONUtil.toJsonStr(messageSync);
        // 离线消息
        List<Long> receiveList = Arrays.asList(pushFrom.getUserId());
        // 异步执行
        ThreadUtil.execAsync(() -> {
            redisOther.pushMsg(pushFrom.getSyncId(), pushFrom.getGroupId(), receiveList, message, _getDateList());
        });
        // 发送消息
        this.sendMsg(receiveList, message, ChannelEnum.MSG);
    }

    @Override
    public void pushGroup(PushFrom pushFrom, PushGroup pushGroup, List<Long> receiveList, String
            content, PushMsgTypeEnum msgType) {
        // 发送消息
        this.pushMsg(pushFrom, pushGroup, receiveList, content, msgType);
    }

    /**
     * 推送消息
     */
    private void pushMsg(PushFrom pushFrom, PushGroup pushGroup, List<Long> receiveList, String
            content, PushMsgTypeEnum msgType) {
        // 消息组装
        PushMessageMsg messageMsg = new PushMessageMsg(pushFrom, pushGroup, msgType, content);
        // 格式消息
        String message = JSONUtil.toJsonStr(messageMsg);
        // 离线消息
        String groupId = pushFrom.getGroupId();
        if (pushGroup != null) {
            groupId = NumberUtil.toStr(pushGroup.getGroupId());
        }
        // 异步执行
        String finalGroupId = groupId;
        ThreadUtil.execAsync(() -> {
            redisOther.pushMsg(pushFrom.getMsgId(), finalGroupId, receiveList, message, _getDateList());
        });
        // 发送消息
        this.sendMsg(receiveList, message, ChannelEnum.MSG);
    }

    /**
     * 获取时间列表
     */
    private List<String> _getDateList() {
        List<String> dataList = new ArrayList<>();
        DateTime now = DateUtil.date();
        for (int i = 0; i < 5; i++) {
            dataList.add(DateUtil.format(DateUtil.offsetDay(now, i), DatePattern.PURE_DATE_PATTERN));
        }
        return dataList;
    }

    @Override
    public void pushBadger(Long receiveId, PushBadgerEnum badgerEnum, List<Long> principal) {
        // 组装key
        String redisKey = StrUtil.format(badgerEnum.getType(), receiveId);
        // 集合判空
        if (!CollectionUtils.isEmpty(principal)) {
            List<String> dataList = principal.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());
            redisUtils.sAdd(redisKey, dataList, PushConstant.PUSH_TIME, TimeUnit.DAYS);
        }
        // 查询数量
        String value = redisUtils.sSize(redisKey).toString();
        String label = badgerEnum.getCode();
        PushSetting setting = new PushSetting(PushSettingEnum.BADGER, receiveId, label, value);
        // 发送消息
        this.pushSetting(setting, Arrays.asList(receiveId));
    }

    /**
     * 发送消息
     */
    private void sendMsg(Collection receiveList, String message, ChannelEnum channelEnum) {
        // 集合判空
        if (!ChannelEnum.ALL.equals(channelEnum) && CollectionUtils.isEmpty(receiveList)) {
            return;
        }
        // 组装消息
        JSONObject jsonObject = new JSONObject()
                .set("receiveList", receiveList)
                .set("content", message)
                .set("channel", channelEnum);
        String content = JSONUtil.toJsonStr(jsonObject);
        // 发送消息
        pushProvider.convertAndSend(content);
        // 发送消息
        pushProvider.convertAndSend(receiveList, message);
    }

    @Override
    public List<JSONObject> pullMsg(Long userId, String lastId, int limit) {
        // 获取消息
        List<String> redisKeys = this.pullMsgId(userId, lastId, limit);
        if (CollectionUtils.isEmpty(redisKeys)) {
            return new ArrayList<>();
        }
        // 获取消息
        List<String> dataList = redisOther.multiGet(redisKeys);
        // 集合判空
        if (CollectionUtils.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        // 消息转换
        return dataList.stream().collect(ArrayList::new, (x, y) -> {
            if (!StringUtils.isEmpty(y)) {
                x.add(JSONUtil.parseObj(y));
            }
        }, ArrayList::addAll);
    }

    /**
     * 获取消息keys
     * eg:[push:msg:123456789]
     */
    private List<String> pullMsgId(Long userId, String lastId, int limit) {
        // 非空组装
        if (StringUtils.isEmpty(lastId)) {
            lastId = "0";
        }
        // 组装消息
        String date = DateUtil.format(DateUtil.date(), DatePattern.PURE_DATE_PATTERN);
        String redisKey = StrUtil.format(PushConstant.PUSH_USER, date, userId);
        // 获取消息keys, eg: [1,2,3,4]
        List<String> dataList = redisOther.lRange(redisKey);
        if (CollectionUtils.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        // 增加数据
        dataList.add(0, lastId);
        // 数据排序
        Collections.sort(dataList);
        // 计算角标
        Integer index = ListUtil.lastIndexOf(dataList, lastId::equals) + 1;
        // 过滤数据
        dataList = ListUtil.sub(dataList, index, index + limit);
        // 组装消息
        return dataList.stream().collect(ArrayList::new, (x, msgId) -> {
            x.add(StrUtil.format(PushConstant.PUSH_MSG, msgId));
        }, ArrayList::addAll);
    }

    @Override
    public void removeMsg(Long userId, List<String> dataList) {
        _getDateList().forEach(date -> {
            String redisKey = StrUtil.format(PushConstant.PUSH_USER, date, userId);
            redisOther.lRemove(redisKey, dataList);
        });
    }

    @Override
    public void removeMsg(Long userId) {
        _getDateList().forEach(date -> {
            String redisKey = StrUtil.format(PushConstant.PUSH_USER, date, userId);
            redisOther.delete(redisKey);
        });
    }

    @Override
    public void clearMsg(Long userId, Long groupId) {
        String groupKey = StrUtil.format(PushConstant.PUSH_GROUP, groupId);
        if (!userId.equals(groupId)) {
            if (groupId.longValue() < 99999L) {
                groupKey = StrUtil.format(PushConstant.PUSH_GROUP, groupId + "" + userId);
            }
            if (userId.longValue() < 99999L) {
                groupKey = StrUtil.format(PushConstant.PUSH_GROUP, userId + "" + groupId);
            }
        }
        // 获取消息keys, eg: [1,2,3,4]
        List<String> messageList = redisOther.lRange(groupKey);
        // 计算用户keys
        _getDateList().forEach(date -> {
            String redisKey = StrUtil.format(PushConstant.PUSH_USER, date, userId);
            // 删除
            redisOther.lRemove(redisKey, messageList);
        });
    }

    @Override
    public void clearMsg(Long groupId) {
        // 获取消息keys, eg: [1,2,3,4]
        List<String> dataList = redisOther.lRange(StrUtil.format(PushConstant.PUSH_GROUP, groupId));
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        // 删除消息
        List<String> array = new ArrayList<>();
        dataList.forEach(data -> {
            array.add(StrUtil.format(PushConstant.PUSH_MSG, data));
        });
        // 删除key
        redisOther.delete(array);
    }

    @Override
    public void recallMsg(List<String> dataList) {
        // 组装key1
        List<String> array = new ArrayList<>();
        dataList.forEach(data -> {
            array.add(StrUtil.format(PushConstant.PUSH_MSG, data));
        });
        // 删除key
        redisOther.delete(array);
    }

    @Override
    public void editMsg(Long msgId, Long syncId, JSONObject content) {
        // 组装
        List<Long> dataList = Arrays.asList(msgId, syncId);
        // 循环
        dataList.forEach(data -> {
            String redisKey = StrUtil.format(PushConstant.PUSH_MSG, data);
            Object redisValue = redisOther.get(redisKey);
            if (redisValue != null) {
                JSONObject jsonObject = JSONUtil.parseObj(redisValue);
                jsonObject.getJSONObject("pushData").set("content", content);
                redisOther.set(redisKey, JSONUtil.toJsonStr(jsonObject), PushConstant.PUSH_TIME, TimeUnit.DAYS);
            }
        });
    }

}
