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
import com.platform.modules.push.dto.*;
import com.platform.modules.push.enums.*;
import com.platform.modules.push.message.*;
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
    public void pushMoment(PushFrom pushFrom,PushMoment setting, List<Long> receiveList) {
        // 消息组装
        PushMessageMoment messageSetting = new PushMessageMoment(pushFrom,setting,PushMomentEnum.MOMENT);
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
    public void pushMomentSync(PushFrom pushFrom, PushMoment pushMoment, List<Long> receiveList,PushMomentEnum msgType) {
        // 消息组装
        PushMomentSync messageSync = new PushMomentSync(pushFrom, pushMoment,msgType);
        // 格式消息
        String message = JSONUtil.toJsonStr(messageSync);
        // 离线消息
        //List<Long> receiveList = Arrays.asList(pushFrom.getUserId());
        // 异步执行
        ThreadUtil.execAsync(() -> {
            redisOther.pushMomentMsg(pushFrom.getSyncId(),  receiveList, message, _getDateList());
        });
        // 发送消息
        this.sendMsg(receiveList, message, ChannelEnum.MSG);
    }

    /**
     * 存储异步消息不发送
     * @param pushFrom
     * @param pushMoment
     * @param receiveList
     * @param msgType
     * @param onlysave
     */
    @Override
    public void pushMomentSync(PushFrom pushFrom, PushMoment pushMoment, List<Long> receiveList,PushMomentEnum msgType,Boolean onlysave) {
        // 消息组装
        PushMomentSync messageSync = new PushMomentSync(pushFrom, pushMoment,msgType);
        // 格式消息
        String message = JSONUtil.toJsonStr(messageSync);
        // 离线消息
        // 异步执行
        ThreadUtil.execAsync(() -> {
            redisOther.pushMomentMsg(pushFrom.getSyncId(),  receiveList, message, _getDateList());
        });
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

    @Override
    public void pushBadger(List<Long> receiveList, PushBadgerEnum badgerEnum, boolean principal) {
        // 遍历所有接收者
        for (Long receiveId : receiveList) {
            // 为每个接收者单独组装计数器的Redis键
            String redisKey = StrUtil.format(badgerEnum.getType(), receiveId);
            Long count=0L;
            // 集合判空
            if (principal) {
                // 核心修改：使用Redis的INCR命令实现数字自动+1（原子操作）
                // 如果键不存在，会先初始化为0再+1，结果为1；如果已存在，直接+1
                count=redisUtils.increment(redisKey,1,PushConstant.PUSH_TIME, TimeUnit.DAYS);
            }
            // 查询当前计数器的值（即最新的数字）
            String value = count.toString();
            String label = badgerEnum.getCode();

            // 为当前接收者创建推送设置
            PushSetting setting = new PushSetting(PushSettingEnum.BADGER, receiveId, label, value);

            // 发送消息
            this.pushSetting(setting, Arrays.asList(receiveId));
        }
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
     * 拉取朋友圈消息
     */
    @Override
    public List<JSONObject> pullMomentMsg(Long userId, String lastId, int limit) {
        // 获取消息
        List<String> redisKeys = this.pullMomentMsgId(userId, lastId, limit);
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

    /**
     * 获取朋友圈消息keys
     * eg:[push:moment:123456789]
     */
    private List<String> pullMomentMsgId(Long userId, String lastId, int limit) {
        // 非空组装
        if (StringUtils.isEmpty(lastId)) {
            lastId = "0";
        }
        // 组装消息
        String date = DateUtil.format(DateUtil.date(), DatePattern.PURE_DATE_PATTERN);
        String redisKey = StrUtil.format(PushConstant.PUSH_USER_MOMENT, date, userId);
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
            x.add(StrUtil.format(PushConstant.PUSH_MOMENT_MSG, msgId));
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


    /**
     * 根据接收者ID发送Redis中存储的朋友圈消息
     * @param receiveId 接收者ID（单个接收者）
     * @param lastId 最后一条消息ID（用于分页，空则发送所有）
     * @param limit 发送数量限制（0则发送所有）
     */
    @Override
    public void pushStoredMomentMsg(String receiveId, String lastId, int limit) {
        // 参数校验
        if (receiveId == null) {
            log.warn("推送存储的朋友圈消息失败：接收者ID为空");
            return;
        }

        // 1. 根据接收者ID获取消息ID列表
        List<String> msgIdList = getMomentMsgIdsByReceiveId(receiveId, lastId, limit > 0 ? limit : Integer.MAX_VALUE);
        if (CollectionUtils.isEmpty(msgIdList)) {
            log.info("接收者[{}]无待发送的存储消息", receiveId);
            return;
        }

        // 2. 批量获取消息内容
        List<String> redisKeys = msgIdList.stream()
                .map(msgId -> StrUtil.format(PushConstant.PUSH_MOMENT_MSG, msgId))
                .collect(Collectors.toList());
        List<String> messageList = redisOther.multiGet(redisKeys);

        // 3. 发送消息（接收者列表为单个用户）
        List<Long> singleReceiveList = Collections.singletonList(Long.parseLong(receiveId));
        for (int i = 0; i < messageList.size(); i++) {
            String message = messageList.get(i);
            if (StringUtils.isEmpty(message)) {
                continue;
            }
            // 发送消息并记录日志
            this.sendMsg(singleReceiveList, message, ChannelEnum.MSG);
            log.debug("已向接收者[{}]推送存储的朋友圈消息，消息ID:{}", receiveId, msgIdList.get(i));
        }
    }

    /**
     * 批量向多个接收者发送各自存储的朋友圈消息
     * @param receiveIds 接收者ID列表
     * @param limit 每个接收者的消息数量限制
     */
    @Override
    public void pushStoredMomentMsg(List<String> receiveIds, int limit) {
        if (CollectionUtils.isEmpty(receiveIds)) {
            log.warn("推送存储的朋友圈消息失败：接收者列表为空");
            return;
        }

        // 遍历所有接收者，分别发送其对应的消息
        receiveIds.forEach(receiveId ->
                pushStoredMomentMsg(receiveId, null, limit)
        );
    }



    /**
     * 根据接收者ID获取朋友圈消息ID列表（支持分页）
     */
    private List<String> getMomentMsgIdsByReceiveId(String receiveId, String lastId, int limit) {
        // 1. 获取存储消息ID的日期列表（与存储时的日期逻辑一致，取未来5天）
        List<String> dateList = _getDateList();

        // 2. 构建接收者每天的消息ID列表Key（格式：push:user:moment:{date}:{receiveId}）
        List<String> redisKeys = dateList.stream()
                .map(date -> StrUtil.format(PushConstant.PUSH_USER_MOMENT, date, receiveId))
                .collect(Collectors.toList());

        // 3. 从Redis列表中获取消息ID（倒序取最新，支持分页）
        List<String> allMsgIds = new ArrayList<>();
        for (String key : redisKeys) {
            // 获取列表所有元素（实际可根据Redis列表长度优化，避免全量查询）
            List<String> msgIds = redisOther.lRange(key);
            // 倒序排列（因为存储时用rightPush，最新的在尾部）
            Collections.reverse(msgIds);
            allMsgIds.addAll(msgIds);
        }

        // 4. 去重并按时间排序（消息ID通常为递增，直接排序即可）
        List<String> uniqueMsgIds = allMsgIds.stream()
                .distinct()
                .sorted(Collections.reverseOrder()) // 最新的在前
                .collect(Collectors.toList());

        // 5. 处理分页（根据lastId定位起始位置）
        int startIndex = 0;
        if (StringUtils.hasText(lastId)) {
            startIndex = uniqueMsgIds.indexOf(lastId);
            if (startIndex == -1) {
                startIndex = 0; // 未找到则从开头开始
            } else {
                startIndex++; // 从lastId的下一条开始
            }
        }

        int endIndex = Math.min(startIndex + limit, uniqueMsgIds.size());
        return uniqueMsgIds.subList(startIndex, endIndex);
    }

    @Override
    public List<JSONObject> getStoredMomentMsgByReceiveId(String receiveId, String lastId, int limit) {
        // 1. 获取接收者对应的消息ID列表（按日期分组存储）
        List<String> msgIdList = this.getMomentMsgIdsByReceiveId(receiveId, lastId, limit);
        if (CollectionUtils.isEmpty(msgIdList)) {
            return new ArrayList<>();
        }

        // 2. 根据消息ID列表查询Redis中的消息内容
        List<String> redisKeys = msgIdList.stream()
                .map(msgId -> StrUtil.format(PushConstant.PUSH_MOMENT_MSG, msgId))
                .collect(Collectors.toList());

        List<String> messageList = redisOther.multiGet(redisKeys);
        if (CollectionUtils.isEmpty(messageList)) {
            return new ArrayList<>();
        }

        // 3. 转换为JSONObject并返回
        return messageList.stream()
                .filter(StringUtils::hasText)
                .map(JSONUtil::parseObj)
                .collect(Collectors.toList());
    }



}
